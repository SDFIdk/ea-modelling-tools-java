package dk.gov.data.geo.datamodellingtools.scriptmanagement.impl;

import static net.sf.saxon.s9api.streams.Predicates.eq;
import static net.sf.saxon.s9api.streams.Predicates.some;
import static net.sf.saxon.s9api.streams.Steps.attribute;
import static net.sf.saxon.s9api.streams.Steps.child;
import static net.sf.saxon.s9api.streams.Steps.descendant;
import static net.sf.saxon.s9api.streams.Steps.text;

import dk.gov.data.geo.datamodellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.geo.datamodellingtools.exception.DataModellingToolsException;
import dk.gov.data.geo.datamodellingtools.model.Script;
import dk.gov.data.geo.datamodellingtools.model.ScriptGroup;
import dk.gov.data.geo.datamodellingtools.scriptmanagement.ScriptManager;
import dk.gov.data.geo.datamodellingtools.utils.FolderAndFileUtils;
import dk.gov.data.geo.datamodellingtools.utils.XmlAndXsltUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.TooManyStaticImports")
public class ScriptManagerImpl implements ScriptManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScriptManagerImpl.class);

  /**
   * Repository#SQLQuery(String) returns an XML formatted string value of the resulting record set.
   * It has the following structure (presented as XPath): /EADATA/Dataset_0/Data/Row and uses UTF-8
   * encoding.
   */
  public static final String ELEMENT_NAME_ROW_SQL_QUERY_RESULT = "Row";

  /**
   * Metadata on a script is stored as followed in EA: &lt;Script Name="Name of script"
   * Type="Internal" Language="JavaScript|VBScript|JScript"/&gt; .
   */
  private static final String ELEMENT_NAME_SCRIPT_IN_NOTES = "Script";
  private static final String ATTRIBUTE_NAME_SCRIPT_LANGUAGE = "Language";
  private static final String ATTRIBUTE_NAME_SCRIPT_NAME = "Name";

  private EnterpriseArchitectWrapper eaWrapper;

  public ScriptManagerImpl(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public void exportScripts(String scriptGroupNameOrRegex, File folder)
      throws DataModellingToolsException {
    Validate.notNull(scriptGroupNameOrRegex);
    FolderAndFileUtils.validateAndCreateFolderIfNeeded(folder);
    LOGGER.info("Start exporting scripts");

    String queryResultString = retrieveScriptsAndScriptGroupsFromEa(scriptGroupNameOrRegex);
    saveScriptsAsSeparateFilesPerScriptGroup(folder, queryResultString);
    transformToReferenceData(queryResultString, new File(folder, "referencedata_scripts.xml"));
    LOGGER.info("Finished exporting scripts");
  }

  private void saveScriptsAsSeparateFilesPerScriptGroup(File folder, String queryResultString)
      throws DataModellingToolsException {
    XdmNode queryResultNode = createNodeFromXmlFormattedString(queryResultString);
    List<XdmNode> scriptGroupRows =
        queryResultNode.select(descendant(ELEMENT_NAME_ROW_SQL_QUERY_RESULT)
            .where(some(child("Notes"), xmlUnEscapedStringValueStartsWith("<Group")))).asList();
    for (XdmNode scripGroupRow : scriptGroupRows) {
      ScriptGroup scriptGroup = createScriptGroup(scripGroupRow);
      File scriptFolder = createScriptFolderIfNeeded(folder, scriptGroup.getName());
      LOGGER.info("Starting exporting scripts for script group " + scriptGroup.toString()
          + " to folder " + scriptFolder.getAbsolutePath());
      List<XdmNode> scriptRowsForScriptGroup =
          queryResultNode.select(descendant(ELEMENT_NAME_ROW_SQL_QUERY_RESULT)
              .where(some(child("ScriptAuthor"), eq(scriptGroup.getId())))).asList();
      LOGGER.info("Number of scripts in " + scriptGroup.toString() + ": "
          + scriptRowsForScriptGroup.size());
      for (XdmNode scriptRow : scriptRowsForScriptGroup) {
        Script script = createScript(scriptGroup, scriptRow);
        LOGGER.info(script.toString());
        File scriptFile = new File(scriptFolder, script.getFileName());
        try {
          FileUtils.writeStringToFile(scriptFile, script.getContents(), StandardCharsets.UTF_8);
          LOGGER.info(scriptFile.getAbsolutePath() + " written.");
        } catch (IOException e) {
          throw new DataModellingToolsException(
              "Could not write content to " + scriptFile.getPath() + e.getMessage(), e);
        }
      }
      inspectScriptFolderContents(scriptFolder, scriptRowsForScriptGroup);
    }
  }

  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  private void inspectScriptFolderContents(File scriptFolder,
      List<XdmNode> scriptRowsForScriptGroup) {
    assert scriptFolder.isDirectory();
    if (scriptFolder.list().length > scriptRowsForScriptGroup.size()) {
      LOGGER.warn(scriptFolder.getPath()
          + " contains more files than the number of scripts that was written,"
          + " has a script been deleted? If so, delete this file as well on the"
          + " file system and in the version control system");
    }
  }

  private Script createScript(ScriptGroup scriptGroup, XdmNode scriptRow)
      throws DataModellingToolsException {
    String scriptId = scriptRow.select(child("ScriptName").then(text())).asString();
    String scriptNotes = scriptRow.select(child("Notes").then(text())).asString();
    XdmNode scriptMetadataNode = createNodeFromXmlFormattedString(scriptNotes);
    String scriptName = scriptMetadataNode
        .select(
            descendant(ELEMENT_NAME_SCRIPT_IN_NOTES).then(attribute(ATTRIBUTE_NAME_SCRIPT_NAME)))
        .asString();
    String scriptLanguage = scriptMetadataNode.select(
        descendant(ELEMENT_NAME_SCRIPT_IN_NOTES).then(attribute(ATTRIBUTE_NAME_SCRIPT_LANGUAGE)))
        .asString();
    String scriptContents = scriptRow.select(child("Script").then(text())).asString();
    /*
     * Replace new line with carriage return + new line, in order to match the encoding of the
     * scripts when saved as files through EA's GUI in Windows.
     */
    String scriptContents2 = scriptContents.replaceAll("(?m)\n", "\r\n");
    Script script =
        new Script(scriptId, scriptName, scriptLanguage, scriptGroup.getId(), scriptContents2);
    return script;
  }

  private ScriptGroup createScriptGroup(XdmNode scripGroupRow) {
    String scriptGroupId = scripGroupRow.select(child("ScriptName").then(text())).asString();
    String scriptGroupName = scripGroupRow.select(child("Script").then(text())).asString();
    ScriptGroup scriptGroup = new ScriptGroup(scriptGroupId, scriptGroupName);
    return scriptGroup;
  }

  private String retrieveScriptsAndScriptGroupsFromEa(String scriptGroupNameOrRegex)
      throws DataModellingToolsException {
    /*
     * Requires probably Jet 4.0 (see EA settings).
     * 
     * From the Jscript used earlier: For some reason, union all gives an error when using
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
    String queryResultString = executeSqlQuery(sqlQuery);
    return queryResultString;
  }

  private Predicate<XdmItem> xmlUnEscapedStringValueStartsWith(String value) {
    return item -> StringEscapeUtils.unescapeXml(item.getStringValue()).startsWith(value);
  }

  private String executeSqlQuery(String query) throws DataModellingToolsException {
    LOGGER.debug("Executing query " + query);
    String resultSqlQueryAsXmlFormattedString = eaWrapper.sqlQuery(query);
    LOGGER.debug("Query result: " + resultSqlQueryAsXmlFormattedString);
    return resultSqlQueryAsXmlFormattedString;
  }

  private File createScriptFolderIfNeeded(File folder, String scriptGroupName)
      throws DataModellingToolsException {
    File scriptFolder = new File(folder, scriptGroupName);
    FolderAndFileUtils.validateAndCreateFolderIfNeeded(scriptFolder);
    return scriptFolder;
  }

  private XdmNode createNodeFromXmlFormattedString(String xmlFormattedString)
      throws DataModellingToolsException {
    // Result from SQLQuery starts with <?xml version="1.0"?>, thus UTF-8 encoding.
    try {
      DocumentBuilder documentBuilder = XmlAndXsltUtils.getProcessor().newDocumentBuilder();
      XdmNode queryResultAsXdmNode = documentBuilder.build(
          new StreamSource(IOUtils.toInputStream(xmlFormattedString, StandardCharsets.UTF_8)));
      return queryResultAsXdmNode;
    } catch (SaxonApiException e) {
      LOGGER.info(xmlFormattedString);
      throw new DataModellingToolsException(
          "An exception occurred while trying to deal with an SQL query result from EA: "
              + e.getMessage(),
          e);
    }
  }

  private void transformToReferenceData(String xml, File referenceData)
      throws DataModellingToolsException {
    XmlAndXsltUtils.transformXml(xml, "/scriptmanagement/transform_to_ea_reference_data.xsl",
        referenceData);
  }

}
