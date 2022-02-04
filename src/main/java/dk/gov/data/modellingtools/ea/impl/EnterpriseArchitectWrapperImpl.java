package dk.gov.data.modellingtools.ea.impl;

import static net.sf.saxon.s9api.streams.Steps.child;
import static net.sf.saxon.s9api.streams.Steps.descendant;
import static net.sf.saxon.s9api.streams.Steps.text;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.ConnectorType;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.utils.XmlAndXsltUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.saxon.s9api.XdmNode;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Connector;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;
import org.sparx.Services;

/**
 * Implementation of the wrapper for Enterprise Architect.
 */
public class EnterpriseArchitectWrapperImpl implements EnterpriseArchitectWrapper {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(EnterpriseArchitectWrapperImpl.class);

  private static final String TASKLIST_EA = "TASKLIST /V /FO CSV /NH /FI \"IMAGENAME eq EA.exe\"";

  /**
   * Regex for contents of column t_xref.description (where column name = 'Stereotypes').
   * 
   * <p>Matches e.g. both
   * <code>@STEREO;Name=DKEgenskab;GUID={60F575A8-BB25-4f93-98A1-2751EE93B5C5};FQName=Grunddata::DKEgenskab;@ENDSTEREO;</code>
   * and <code>@STEREO;Name=DKEgenskab;FQName=Grunddata::DKEgenskab;@ENDSTEREO;</code></p>
   */
  private static final String REGEX_STEREOTYPES =
      "@STEREO;Name=[^;]*;(?:GUID=[^;]*;)?FQName=([^;]*);@ENDSTEREO;";

  private Repository eaRepository;

  public EnterpriseArchitectWrapperImpl(int eaProcessId) throws ModellingToolsException {
    super();
    this.eaRepository = retrieveRepository(eaProcessId);
  }

  private Repository retrieveRepository(int eaProcessId) throws ModellingToolsException {
    if (OS.isFamilyWindows()) {
      String output = queryTaskListForEaProcesses(eaProcessId);
      validateOutput(eaProcessId, output);
      return Services.GetRepository(eaProcessId);
    } else {
      throw new ModellingToolsException("This application can currently only be used in Windows");
    }
  }

  private String queryTaskListForEaProcesses() throws ModellingToolsException {
    String taskListCommand = TASKLIST_EA;
    return executeTaskListCommand(taskListCommand);
  }

  private String queryTaskListForEaProcesses(int eaProcessId) throws ModellingToolsException {
    String taskListCommand = TASKLIST_EA + " /FI \"PID eq " + eaProcessId + "\"";
    return executeTaskListCommand(taskListCommand);
  }

  private String executeTaskListCommand(String taskListCommand) throws ModellingToolsException {
    try (ByteArrayOutputStream normalAndErrorOutputStream = new ByteArrayOutputStream()) {
      CommandLine cmdLine = CommandLine.parse(taskListCommand);
      DefaultExecutor executor = new DefaultExecutor();
      executor.setStreamHandler(new PumpStreamHandler(normalAndErrorOutputStream));
      int exitValue = executor.execute(cmdLine);
      LOGGER.debug("Exit value {} for {}", exitValue, taskListCommand);
      String output = normalAndErrorOutputStream.toString(Charset.defaultCharset());
      LOGGER.debug(output);
      return output;
    } catch (IOException e) {
      throw new ModellingToolsException(
          "An error occurred when trying to execute " + taskListCommand, e);
    }
  }

  private void validateOutput(int eaProcessId, String output) throws ModellingToolsException {
    if (output.indexOf("No tasks are running which match the specified criteria") != -1) {
      LOGGER.info("Output received:\r\n{}", output);
      LOGGER.info("All running EA processes: \r\n{}", queryTaskListForEaProcesses());
      throw new ModellingToolsException("No EA process found with pid " + eaProcessId
          + ", check the previous logging output to see the currently running EA processes");
    }
  }

  @Override
  public void writeToScriptWindow(String message) throws ModellingToolsException {
    /*
     * 1st parameter: specifies the tab on which to display the text; 2nd parameter: specifies the
     * text to display; 3rd parameter: specifies a numeric ID value to associate with this output
     * item for further handling by Add-Ins; can be set to 0 if no handling is required
     */
    this.eaRepository.WriteOutput("Script", message, 0);
  }

  @Override
  public String sqlQuery(String query) throws ModellingToolsException {
    LOGGER.debug("Executing query {}", query);
    String resultSqlQueryAsXmlFormattedString = this.eaRepository.SQLQuery(query);
    LOGGER.trace("Query result: {}", resultSqlQueryAsXmlFormattedString);
    return resultSqlQueryAsXmlFormattedString;
  }

  @Override
  public Package getPackageByGuid(String packageGuid) throws ModellingToolsException {
    return this.eaRepository.GetPackageByGuid(packageGuid);
  }

  @Override
  public Element getElementById(int id) {
    return this.eaRepository.GetElementByID(id);
  }

  @Override
  public MultiValuedMap<String, String> retrieveAttributeFqStereotypes(Package umlPackage)
      throws ModellingToolsException {
    String query =
        "select a.ea_guid as id, x.description as stereotypes from t_object o inner join "
            + "(t_attribute a inner join t_xref x on a.ea_guid = x.client) "
            + "on o.object_id = a.object_id where o.package_id in ("
            + getPackageIdStringForSql(umlPackage) + ") and x.Name ='Stereotypes'";
    MultiValuedMap<String, String> multiValuedMap = retrieveFqStereotypes(query);
    return multiValuedMap;
  }

  /**
   * The query result must include columns with name ea_guid and stereotypes.
   */
  private MultiValuedMap<String, String> retrieveFqStereotypes(String query)
      throws ModellingToolsException {
    String queryResult = sqlQuery(query);
    XdmNode queryResultNode = XmlAndXsltUtils.createNodeFromXmlFormattedString(queryResult);
    Pattern pattern = Pattern.compile(REGEX_STEREOTYPES);
    List<XdmNode> rowNodes = queryResultNode.select(descendant("Row")).asList();
    MultiValuedMap<String, String> multiValuedMap = new ArrayListValuedHashMap<>();
    for (XdmNode row : rowNodes) {
      String guid = row.select(child("id").then(text())).asString();
      String stereotypesString = row.select(child("stereotypes").then(text())).asString();
      Matcher matcher = pattern.matcher(stereotypesString);
      if (matcher.lookingAt()) {
        for (int i = 1; i <= matcher.groupCount(); i++) {
          String fqStereotype = matcher.group(i);
          multiValuedMap.put(guid, fqStereotype);
        }
      }
    }
    return multiValuedMap;
  }

  @Override
  public MultiValuedMap<String, String> retrieveConnectorEndFqStereotypes()
      throws ModellingToolsException {
    String query = "select Client & IIF(Type = 'connectorSrcEnd property', '"
        + EaConnectorEnd.ID_SUFFIX_SOURCE + "', '" + EaConnectorEnd.ID_SUFFIX_TARGET
        + "') as id, Description as stereotypes from t_xref x "
        + "where x.Name = 'Stereotypes' and x.Type in ('connectorSrcEnd property', 'connectorDestEnd property')";
    MultiValuedMap<String, String> multiValuedMap = retrieveFqStereotypes(query);
    return multiValuedMap;
  }

  private String getPackageIdStringForSql(Package umlPackage) {
    Collection<Integer> packageIds = new ArrayList<>();
    packageIds.add(umlPackage.GetPackageID());
    for (Package subPackage : EaModelUtils.getSubpackages(umlPackage)) {
      packageIds.add(subPackage.GetPackageID());
    }
    String packageIdString = StringUtils.join(packageIds, ',');
    LOGGER.debug("Package ids: {}", packageIdString);
    return packageIdString;
  }

  @Override
  public boolean isConnectorAssociationAndControlledInSamePackageAsElement(Connector connector,
      Element element) {
    return eaRepository.GetElementByID(connector.GetClientID()).GetPackageID() == eaRepository
        .GetElementByID(connector.GetSupplierID()).GetPackageID()
        && (connector.GetType() == ConnectorType.ASSOCIATION.getEaType()
            || connector.GetType() == ConnectorType.AGGREGATION.getEaType())
        || connector.GetClientID() == element.GetElementID()
            && connector.GetType() == ConnectorType.ASSOCIATION.getEaType()
        || connector.GetSupplierID() == element.GetElementID()
            && connector.GetType() == ConnectorType.AGGREGATION.getEaType();
  }

}
