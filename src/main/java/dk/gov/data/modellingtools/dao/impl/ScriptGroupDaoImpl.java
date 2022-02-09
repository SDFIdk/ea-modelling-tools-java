package dk.gov.data.modellingtools.dao.impl;

import static net.sf.saxon.s9api.streams.Predicates.eq;
import static net.sf.saxon.s9api.streams.Predicates.some;
import static net.sf.saxon.s9api.streams.Steps.attribute;
import static net.sf.saxon.s9api.streams.Steps.child;
import static net.sf.saxon.s9api.streams.Steps.descendant;
import static net.sf.saxon.s9api.streams.Steps.text;

import dk.gov.data.modellingtools.dao.ScriptGroupDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Script;
import dk.gov.data.modellingtools.model.ScriptGroup;
import dk.gov.data.modellingtools.utils.XmlAndXsltUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves scripts group from the Enterprise Architect model.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class ScriptGroupDaoImpl implements ScriptGroupDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScriptGroupDaoImpl.class);

  private EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public ScriptGroupDaoImpl(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public List<ScriptGroup> findAllIncludingScripts(String scriptGroupNameOrRegex)
      throws ModellingToolsException {
    String queryResultString = retrieveScriptGroupsAndScriptsWithSqlQuery(scriptGroupNameOrRegex);
    return buildScriptGroupsWithScripts(queryResultString);
  }

  @Override
  public void saveAllIncludingScriptsAsEaReferenceData(String scriptGroupNameOrRegex,
      File referenceData) throws ModellingToolsException {
    String queryResultString = retrieveScriptGroupsAndScriptsWithSqlQuery(scriptGroupNameOrRegex);
    XmlAndXsltUtils.transformXml(queryResultString,
        "/scriptmanagement/transform_to_ea_reference_data.xsl", referenceData);
  }

  // TODO check how expensive this query is, perhaps it should be cached? Is called twice from
  // ScriptManagerImpl
  private String retrieveScriptGroupsAndScriptsWithSqlQuery(String scriptGroupNameOrRegex)
      throws ModellingToolsException {
    /*
     * Requires probably Jet 4.0 (see EA settings).
     * 
     * From the JScript used earlier: For some reason, union all gives an error when using
     * Repository.SQLQuery, therefore two queries instead. The error: EA shows an error box with the
     * following contents: Error: Code=0x0 Source Line : 0; Char : 0 Error description = (null)
     */
    String sqlQuery =
        "select s.ScriptCategory, s.ScriptName, s.ScriptAuthor, s.Notes, s.Script from t_script s"
            + " where s.Script like '" + scriptGroupNameOrRegex
            + "' and s.Notes like '<Group*' union all"
            + " select s.ScriptCategory, s.ScriptName, s.ScriptAuthor, s.Notes, s.Script"
            + " from t_script s inner join t_script s1 on s1.ScriptName = s.ScriptAuthor"
            + " where s1.script like '" + scriptGroupNameOrRegex + "' and s1.Notes like '<Group*'";
    String queryResultString = eaWrapper.sqlQuery(sqlQuery);
    return queryResultString;
  }


  private List<ScriptGroup> buildScriptGroupsWithScripts(String queryResultString)
      throws ModellingToolsException {
    List<ScriptGroup> scriptGroups = new ArrayList<>();
    XdmNode queryResultNode = XmlAndXsltUtils.createNodeFromXmlFormattedString(queryResultString);
    List<XdmNode> scriptGroupRows = queryResultNode.select(
        descendant("Row").where(some(child("Notes"), xmlUnEscapedStringValueStartsWith("<Group"))))
        .asList();
    for (XdmNode scripGroupRow : scriptGroupRows) {
      ScriptGroup scriptGroup = createScriptGroup(scripGroupRow);
      scriptGroups.add(scriptGroup);
      List<XdmNode> scriptRowsForScriptGroup = queryResultNode
          .select(descendant("Row").where(some(child("ScriptAuthor"), eq(scriptGroup.getId()))))
          .asList();
      LOGGER.info("Number of scripts in {}: {}", scriptGroup.toString(),
          scriptRowsForScriptGroup.size());
      List<Script> scripts = new ArrayList<>();
      for (XdmNode scriptRow : scriptRowsForScriptGroup) {
        Script script = createScript(scriptGroup, scriptRow);
        scripts.add(script);
        LOGGER.info(script.toString());
      }
      scriptGroup.setScripts(scripts);
    }
    return scriptGroups;
  }

  /**
   * Creates a {@link Script} based on the XML.
   * 
   * <p>Note: New line is replaced with carriage return + new line, in order to match the encoding
   * of the scripts when saved as files through EA's GUI in Windows.
   */
  private Script createScript(ScriptGroup scriptGroup, XdmNode scriptRow)
      throws ModellingToolsException {
    /*
     * XML element names are strings in this method as opposed to refactored to constants - to
     * enhance readibility. See also file queryresult.xml in src/test/resources for an example.
     */
    String scriptId = scriptRow.select(child("ScriptName").then(text())).asString();
    String scriptMetadataText = scriptRow.select(child("Notes").then(text())).asString();
    XdmNode scriptMetadataNode =
        XmlAndXsltUtils.createNodeFromXmlFormattedString(scriptMetadataText);
    String scriptName =
        scriptMetadataNode.select(descendant("Script").then(attribute("Name"))).asString();
    String scriptLanguage =
        scriptMetadataNode.select(descendant("Script").then(attribute("Language"))).asString();
    String scriptContents = scriptRow.select(child("Script").then(text())).asString();
    String scriptContents2 = scriptContents.replaceAll("(?m)\n", "\r\n");
    Script script =
        new Script(scriptId, scriptName, scriptLanguage, scriptGroup.getId(), scriptContents2);
    return script;
  }

  private ScriptGroup createScriptGroup(XdmNode scriptGroupRow) throws ModellingToolsException {
    /*
     * XML element names are strings in this method as opposed to refactored to constants - to
     * enhance readibility. See also file queryresult.xml in src/test/resources for an example.
     */
    String scriptGroupId = scriptGroupRow.select(child("ScriptName").then(text())).asString();
    String scriptGroupName = scriptGroupRow.select(child("Script").then(text())).asString();
    String scriptGroupMetadataText = scriptGroupRow.select(child("Notes").then(text())).asString();
    XdmNode scriptGroupMetadataNode =
        XmlAndXsltUtils.createNodeFromXmlFormattedString(scriptGroupMetadataText);
    String scriptGroupNotes =
        scriptGroupMetadataNode.select(descendant("Group").then(attribute("Notes"))).asString();
    ScriptGroup scriptGroup = new ScriptGroup(scriptGroupId, scriptGroupName, scriptGroupNotes);
    return scriptGroup;
  }

  private Predicate<XdmItem> xmlUnEscapedStringValueStartsWith(String value) {
    return item -> StringEscapeUtils.unescapeXml(item.getStringValue()).startsWith(value);
  }

}