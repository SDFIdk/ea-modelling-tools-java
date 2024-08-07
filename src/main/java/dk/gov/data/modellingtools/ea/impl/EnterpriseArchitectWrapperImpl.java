package dk.gov.data.modellingtools.ea.impl;

import static net.sf.saxon.s9api.streams.Steps.child;
import static net.sf.saxon.s9api.streams.Steps.descendant;
import static net.sf.saxon.s9api.streams.Steps.text;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.RepositoryType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.saxon.s9api.XdmNode;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;
import org.sparx.Services;

/**
 * Implementation of the wrapper for Enterprise Architect.
 */
public final class EnterpriseArchitectWrapperImpl implements EnterpriseArchitectWrapper {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(EnterpriseArchitectWrapperImpl.class);

  private static final String TASKLIST_EA = "TASKLIST /V /FO CSV /NH /FI \"IMAGENAME eq EA.exe\"";

  /**
   * Name of the shared library (.dll file) needed to be able to connect to the Enterprise Architect
   * Automation Interface with a 32-bit Java.
   */
  private static final String DLL_NAME_EA_AUTOMATION_32_JVM = "SSJavaCOM";

  /**
   * Name of the shared library (.dll file) needed to be able to connect to the Enterprise Architect
   * Automation Interface with a 64-bit Java.
   */
  private static final String DLL_NAME_EA_AUTOMATION_64_JVM = "SSJavaCOM64";

  private Map<String, Package> packages;

  /**
   * Regex for contents of column t_xref.description (where column name = 'Stereotypes').
   * 
   * <p>Matches e.g. both<ul>
   * <li><code>@STEREO;Name=DKEgenskab;GUID={60F575A8-BB25-4f93-98A1-2751EE93B5C5};FQName=Grunddata::DKEgenskab;@ENDSTEREO;</code></li>
   * <li><code>@STEREO;Name=DKEgenskab;FQName=Grunddata::DKEgenskab;@ENDSTEREO;</code></li></ul></p>
   */
  private static final String REGEX_STEREOTYPES =
      "@STEREO;Name=[^;]*;(?:GUID=[^;]*;)?FQName=([^;]*);@ENDSTEREO;";

  private Repository eaRepository;

  /**
   * Constructor.
   *
   * @param eaProcessId the Windows process id of the running EA instance containing the model of
   *        interest
   * @throws ModellingToolsException if the EA repository cannot be retrieved
   */
  public EnterpriseArchitectWrapperImpl(int eaProcessId) throws ModellingToolsException {
    super();
    this.eaRepository = retrieveRepository(eaProcessId);
    this.packages = new HashMap<>();
  }

  /**
   * Constructor.
   */
  public EnterpriseArchitectWrapperImpl(Repository eaRepository) {
    super();
    this.eaRepository = eaRepository;
    this.packages = new HashMap<>();
  }

  /**
   * Constructor to be used when no Windows process id is available, e.g. because EA is not yet
   * running.
   */
  public EnterpriseArchitectWrapperImpl() {
    this.eaRepository = new Repository();
    this.packages = new HashMap<>();
  }

  /**
   * The static initializer of {@link Services} calls {@link System#loadLibrary(String)}.
   *
   * @see System#loadLibrary(String)
   */
  private Repository retrieveRepository(int eaProcessId) throws ModellingToolsException {
    printSelectedSystemProperties();
    if (OS.isFamilyWindows()) {
      String output = queryTaskListForEaProcesses(eaProcessId);
      validateOutput(eaProcessId, output);
      LOGGER.info("Get the EA repository for process id {}", eaProcessId);
      Repository repository = null;
      try {
        // involves a call to System#loadLibrary(String)
        repository = Services.GetRepository(eaProcessId);
      } catch (UnsatisfiedLinkError e) {
        LOGGER.error(e.getMessage(), e);
        // try to see if a more descriptive error appears
        // same logic as in the static initializers of Services and Repository
        String jvmArchitecture = System.getProperty("os.arch");
        String libname;
        if ("x86".equals(jvmArchitecture)) {
          libname = DLL_NAME_EA_AUTOMATION_32_JVM;
        } else {
          libname = DLL_NAME_EA_AUTOMATION_64_JVM;
        }
        try {
          System.loadLibrary(libname);
        } catch (UnsatisfiedLinkError e1) {
          LOGGER.error("Error when trying to load {}, the EA library to connect to Java", libname,
              e1);
        }
        throw new ModellingToolsException("Check the log for more information", e);
      }
      return repository;
    } else {
      throw new ModellingToolsException("This application can only be used in Windows");
    }
  }

  /**
   * Print selected system properties that may be useful for troubleshooting.
   *
   * @see System
   */
  private void printSelectedSystemProperties() {
    LOGGER.info("Java installation directory (java.home): {}", System.getProperty("java.home"));
    LOGGER.info("JVM architecture (os.arch): {}", System.getProperty("os.arch"));
    LOGGER.info("Java runtime name (java.runtime.name): {}",
        System.getProperty("java.runtime.name"));
    LOGGER.info("Java runtime version (java.runtime.version): {}",
        System.getProperty("java.runtime.version"));
    LOGGER.info("Java class path (java.class.path): {}", System.getProperty("java.class.path"));
    LOGGER.info(
        "List of paths to search when loading native libraries, such as {} or {} (java.library.path): {}",
        DLL_NAME_EA_AUTOMATION_32_JVM, DLL_NAME_EA_AUTOMATION_64_JVM,
        System.getProperty("java.library.path"));
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
    Package umlPackage;
    if (packages.containsKey(packageGuid)) {
      umlPackage = packages.get(packageGuid);
    } else {
      umlPackage = this.eaRepository.GetPackageByGuid(packageGuid);
    }
    return umlPackage;
  }

  @Override
  public Element getElementById(int id) {
    return this.eaRepository.GetElementByID(id);
  }

  @Override
  public Element getElementByGuid(String packageGuid) {
    return this.eaRepository.GetElementByGuid(packageGuid);
  }

  @Override
  public Attribute getAttributeByGuid(String attributeGuid) {
    return this.eaRepository.GetAttributeByGuid(attributeGuid);
  }

  @Override
  public Connector getConnectorByGuid(String connectorGuid) {
    return this.eaRepository.GetConnectorByGuid(connectorGuid);
  }

  @Override
  public Collection<String> retrievePackageFqStereotypes(Package umlPackage)
      throws ModellingToolsException {
    String query = "select o.ea_guid as id, x.description as stereotypes "
        + "from t_object o inner join t_xref x on o.ea_guid = x.client where o.ea_guid = '"
        + umlPackage.GetElement().GetElementGUID() + "' and x.Name ='Stereotypes'";
    Collection<String> packageFqStereotypes =
        retrieveFqStereotypes(query).get(umlPackage.GetElement().GetElementGUID());
    LOGGER.debug("Fully qualified stereotypes on {}: {}", EaModelUtils.toString(umlPackage),
        StringUtils.join(packageFqStereotypes, ", "));
    return packageFqStereotypes;
  }

  @Override
  public MultiValuedMap<String, String> retrieveElementFqStereotypes(Package umlPackage)
      throws ModellingToolsException {
    String query =
        "SELECT o.ea_guid as id, x.description as stereotypes FROM t_object o INNER JOIN t_xref x ON o.ea_guid = x.client AND x.name = 'Stereotypes' WHERE o.package_id IN ("
            + getPackageIdStringForSql(umlPackage)
            + ") AND o.object_type IN ('Class', 'DataType', 'Enumeration', 'Interface')";
    MultiValuedMap<String, String> multiValuedMap = retrieveFqStereotypes(query);
    return multiValuedMap;
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
   * The query result must include columns with name "id" and "stereotypes".
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
    LOGGER.trace("Fully-qualified stereotypes: {}", multiValuedMap);
    return multiValuedMap;
  }

  @Override
  public MultiValuedMap<String, String> retrieveConnectorEndFqStereotypes()
      throws ModellingToolsException {
    /*
     * See also class EaConnectorEnd.
     */
    /*
     * The connector GUID starts with a curly brace. The next two characters are removed. Therefore
     * remove three characters in total. String are 1-based indexed in SQLite, so substring(4) is
     * needed.
     */
    String query = "select '{' + IIF(Type = 'connectorSrcEnd property', '"
        + EaConnectorEnd.ID_PREFIX_SOURCE + "', '" + EaConnectorEnd.ID_PREFIX_TARGET
        + "') + substring(Client, 4) as id, Description as stereotypes from t_xref x "
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
    LOGGER.debug("Package and subpackage ids for {}: {}", umlPackage, packageIdString);
    return packageIdString;
  }

  @Override
  public boolean isConnectorAssociationAndControlledInSamePackageAsElement(Connector connector,
      Element element) {
    return eaRepository.GetElementByID(connector.GetClientID()).GetPackageID() == eaRepository
        .GetElementByID(connector.GetSupplierID()).GetPackageID()
        && (ConnectorType.ASSOCIATION.getEaType().equals(connector.GetType())
            || ConnectorType.AGGREGATION.getEaType().equals(connector.GetType()))
        || connector.GetClientID() == element.GetElementID()
            && ConnectorType.ASSOCIATION.getEaType().equals(connector.GetType())
        || connector.GetSupplierID() == element.GetElementID()
            && ConnectorType.AGGREGATION.getEaType().equals(connector.GetType());
  }

  @Override
  public RepositoryType getRepositoryType() {
    String eaRepositoryType = eaRepository.RepositoryType();
    final RepositoryType repositoryType;
    switch (eaRepositoryType) {
      case "JET":
        repositoryType = RepositoryType.EAPX;
        break;
      case "FIREBIRD":
        repositoryType = RepositoryType.FEAP;
        break;
      case "SL3":
        repositoryType = RepositoryType.QEA;
        break;
      default:
        throw new NotImplementedException(
            "This application does not (yet) support repositories of type " + eaRepositoryType);
    }
    return repositoryType;

  }

  @Override
  public boolean transferProject(String sourceFilePath, String targetFilePath)
      throws ModellingToolsException {
    return eaRepository.GetProjectInterface().ProjectTransfer(sourceFilePath, targetFilePath, null);
  }

  @Override
  public int getMajorVersion() throws ModellingToolsException {
    int eaLibraryVersion = eaRepository.GetLibraryVersion();
    LOGGER.info("EA Library version " + eaLibraryVersion);
    if (eaLibraryVersion >= 1000) {
      return Math.floorDiv(eaLibraryVersion, 100);
    } else {
      throw new ModellingToolsException("Unsupported version of EA: " + eaLibraryVersion);
    }
  }

  @Override
  public void terminate() {
    eaRepository.Exit();
    eaRepository.destroy();
    eaRepository = null;
  }

  @Override
  public String getMdgVersion(String mdgId) {
    return eaRepository.GetTechnologyVersion(mdgId);
  }

  @Override
  public void reloadPackage(Package umlPackage) {
    LOGGER.info("Reloading " + EaModelUtils.toString(umlPackage));
    eaRepository.RefreshModelView(umlPackage.GetPackageID());
  }

}
