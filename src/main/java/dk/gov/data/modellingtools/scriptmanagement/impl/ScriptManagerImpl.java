package dk.gov.data.modellingtools.scriptmanagement.impl;

import dk.gov.data.modellingtools.config.FreemarkerTemplateConfiguration;
import dk.gov.data.modellingtools.dao.ScriptGroupDao;
import dk.gov.data.modellingtools.dao.impl.ScriptGroupDaoImpl;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Script;
import dk.gov.data.modellingtools.model.ScriptGroup;
import dk.gov.data.modellingtools.scriptmanagement.ScriptManager;
import dk.gov.data.modellingtools.utils.FolderAndFileUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages scripts.
 */
public class ScriptManagerImpl implements ScriptManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScriptManagerImpl.class);

  private ScriptGroupDao scriptGroupDao;

  public ScriptManagerImpl(EnterpriseArchitectWrapper eaWrapper) {
    this(new ScriptGroupDaoImpl(eaWrapper));
  }

  public ScriptManagerImpl(ScriptGroupDao scriptGroupDao) {
    this.scriptGroupDao = scriptGroupDao;
  }

  @Override
  public void exportScripts(String scriptGroupNameOrRegex, File folder, boolean createDocumentation)
      throws ModellingToolsException {
    Validate.notNull(scriptGroupNameOrRegex,
        "A script group name (may contain wildcards) must be given");
    Validate.notNull(folder, "A folder must be given");

    File referenceData = new File(folder, "referencedata_scripts.xml");
    validateAndCreateOrCleanFolder(folder, referenceData);

    LOGGER.info("Start exporting scripts");

    List<ScriptGroup> scriptGroups = scriptGroupDao.findAllIncludingScripts(scriptGroupNameOrRegex);
    saveScriptsAsSeparateFilesPerScriptGroup(folder, scriptGroups);
    scriptGroupDao.saveAllIncludingScriptsAsEaReferenceData(scriptGroupNameOrRegex, referenceData);
    if (createDocumentation) {
      createScriptDocumentation(scriptGroups, new File(folder, "README.md"));
    }
    LOGGER.info("Finished exporting scripts to {}", folder);
  }

  /**
   * If the folder does not yet exist, create it. If the folder does exist, check whether it is
   * either an empty directory or whether it already contains a reference data file; if that check
   * passes, clean the directory.
   * 
   * <p>The latter check tries to ensure that if the user selected a wrong, already existing folder,
   * its contents are not deleted but an exception is thrown.
   */
  private void validateAndCreateOrCleanFolder(File folder, File referenceData)
      throws ModellingToolsException {
    if (folder.exists()) {
      Validate.isTrue(folder.isDirectory(), folder.getAbsolutePath() + " is not a directory");
      try {
        boolean emptyDirectory = PathUtils.isEmptyDirectory(folder.toPath());
        boolean directoryContainsReferenceData = FileUtils.directoryContains(folder, referenceData);
        Validate.isTrue(emptyDirectory || directoryContainsReferenceData,
            "Cannot export to folder %1$s. It must be either empty or contain reference data. State: empty=%2$b; contains reference data=%3$b",
            folder.getAbsolutePath(), emptyDirectory, directoryContainsReferenceData);
        FileUtils.cleanDirectory(folder);
      } catch (IOException e) {
        throw new ModellingToolsException("Could not validate folder " + folder.toString(), e);
      }
    } else {
      boolean mkdirResult = folder.mkdir();
      if (!mkdirResult) {
        throw new ModellingToolsException("Could not create folder " + folder);
      }
    }
  }


  private void saveScriptsAsSeparateFilesPerScriptGroup(File folder, List<ScriptGroup> scriptGroups)
      throws ModellingToolsException {
    for (ScriptGroup scriptGroup : scriptGroups) {
      File scriptFolder = createScriptFolderIfNeeded(folder, scriptGroup.getName());
      LOGGER.info("Starting exporting scripts for script group {} to folder {}",
          scriptGroup.toString(), scriptFolder.getAbsolutePath());
      List<Script> scripts = scriptGroup.getScripts();
      for (Script script : scripts) {
        LOGGER.info(script.toString());
        File scriptFile = new File(scriptFolder, script.getFileName());
        try {
          FileUtils.writeStringToFile(scriptFile, script.getContents(), StandardCharsets.UTF_8);
          LOGGER.info("{} written.", scriptFile.getAbsolutePath());
        } catch (IOException e) {
          throw new ModellingToolsException(
              "Could not write content to " + scriptFile.getPath() + e.getMessage(), e);
        }
      }
      inspectScriptFolderContents(scriptFolder, scripts);
    }
  }

  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  private void inspectScriptFolderContents(File scriptFolder, List<Script> scripts) {
    Validate.notNull(scriptFolder);
    Validate.notNull(scripts);
    Validate.notNull(scriptFolder.list());
    if (scriptFolder.list().length > scripts.size()) {
      LOGGER.warn(
          "{} contains more files than the number of scripts that was written, has a script been deleted? If so, delete this file as well on the file system and in the version control system.",
          scriptFolder.getPath());
    }
  }

  private File createScriptFolderIfNeeded(File folder, String scriptGroupName)
      throws ModellingToolsException {
    File scriptFolder = new File(folder, scriptGroupName);
    FolderAndFileUtils.validateAndCreateFolderIfNeeded(scriptFolder);
    return scriptFolder;
  }

  private void createScriptDocumentation(List<ScriptGroup> scriptGroups, File scriptDocumentation)
      throws ModellingToolsException {
    FolderAndFileUtils.deleteAndCreate(scriptDocumentation);

    String templateFileName = "script_documentation.ftl";
    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(scriptDocumentation), "UTF-8"))) {
      for (ScriptGroup scriptGroup : scriptGroups) {
        List<Script> scripts = scriptGroup.getScripts();
        for (Script script : scripts) {
          updateScriptMetadataIfPossible(script);
        }
      }

      Map<String, Object> dataForTemplate = new HashMap<>();
      dataForTemplate.put("scriptGroups", scriptGroups);

      // retrieve and populate template
      Template template =
          FreemarkerTemplateConfiguration.INSTANCE.getConfiguration().getTemplate(templateFileName);
      template.process(dataForTemplate, writer);
    } catch (IOException e) {
      throw new ModellingToolsException(
          "Could not write content to " + scriptDocumentation.getPath() + e.getMessage(), e);
    } catch (TemplateException e) {
      throw new ModellingToolsException(
          "Could not process template " + templateFileName + ": " + e.getMessage(), e);
    }

  }

  /**
   * Updates {@link Script#getIsRunnable()}, and {@link Script#getSummary()} and
   * {@link Script#getDescription()} for runnable scripts.
   */
  private void updateScriptMetadataIfPossible(Script script) {
    LOGGER.debug("Parsing script {}", script.getName());
    AstRoot parsedScript =
        getParser().parse(removeEaSpecificConstructsFromScriptContents(script.getContents()),
            script.getFileName(), 1);

    List<Node> nodes = new ArrayList<>();
    CollectionUtils.addAll(nodes, parsedScript.iterator());
    Predicate predicateExpressionStatement = object -> object instanceof ExpressionStatement;
    int numberOfExpressionStatements =
        CollectionUtils.countMatches(nodes, predicateExpressionStatement);
    if (numberOfExpressionStatements == 1) {
      script.setIsRunnable(Boolean.TRUE);
      ExpressionStatement expressionStatement =
          (ExpressionStatement) CollectionUtils.find(nodes, predicateExpressionStatement);
      AstNode expression = expressionStatement.getExpression();
      if (expression instanceof FunctionCall) {
        FunctionCall functionCall = (FunctionCall) expression;
        AstNode target = functionCall.getTarget();
        assert target instanceof Name; // seen during debugging...
        Name targetAsName = (Name) target;
        for (Node node : nodes) {
          if (node instanceof FunctionNode) { // only function nodes are relevant here
            Name functionName = ((FunctionNode) node).getFunctionName();
            if (functionName.getIdentifier().equals(targetAsName.getIdentifier())) {
              String jsDoc = node.getJsDoc();
              if (StringUtils.isBlank(jsDoc)) {
                LOGGER.warn("Function {} has no JSDoc", functionName.getIdentifier());
              } else {
                String jsDocWithoutAsterisks = getJsDocWithoutTagsAndAsterisks(jsDoc);
                script.setSummary(extractTagValueFromJsDoc("@summary", jsDocWithoutAsterisks));
                script.setDescription(extractDescriptionFromJsDoc(jsDocWithoutAsterisks));
              }
            }
          }
        }
      }
    } else if (numberOfExpressionStatements == 0) {
      // probably script with utility functions
      script.setIsRunnable(Boolean.FALSE);
      LOGGER.debug("No expression statements found in {}", script.getFileName());
    } else {
      script.setIsRunnable(Boolean.TRUE);
      LOGGER.warn(
          "More than one expression statement found in {} , no single method found to take the documentation from.",
          script.getFileName());
    }
  }

  private String extractDescriptionFromJsDoc(String jsDocWithoutAsterisks) {
    String description = null;
    String descriptionInTag = extractTagValueFromJsDoc("@description", jsDocWithoutAsterisks);
    if (StringUtils.isBlank(descriptionInTag)) {
      String descriptionInBlock =
          StringUtils.strip(StringUtils.substringBefore(jsDocWithoutAsterisks, "@"));
      if (!StringUtils.isBlank(descriptionInBlock)) {
        description = StringUtils.strip(descriptionInBlock);
      }
    } else {
      description = StringUtils.strip(descriptionInTag);
    }
    return description;
  }

  private String extractTagValueFromJsDoc(String tagName, String jsDocWithoutAsterisks) {
    String tagValue = null;
    // FIXME what if the tag value contains a @, e.g. in a mail adress for an author; use javadoc
    // parser?
    String temp = StringUtils.substringBetween(jsDocWithoutAsterisks, tagName + " ", "@");
    if (!StringUtils.isBlank(temp)) {
      tagValue = StringUtils.strip(temp);
    } else {
      String temp2 = StringUtils.substringAfter(jsDocWithoutAsterisks, tagName + " ");
      if (!StringUtils.isBlank(temp2)) {
        tagValue = StringUtils.strip(temp2);
      }
    }
    return tagValue;
  }

  private String getJsDocWithoutTagsAndAsterisks(String jsDoc) {
    LOGGER.debug("jsdoc: {}", jsDoc);
    Validate.isTrue(jsDoc.startsWith("/**"));
    Validate.isTrue(jsDoc.endsWith("*/"));
    // remove starting /**, ending */ and trailing whitespace
    // what is left is set of lines which may start with *
    // (most of them will, but not necessarily all of them)
    String jsDocWithoutOpeningAndClosingTag =
        StringUtils.substring(jsDoc, 3, jsDoc.length() - 2).strip();
    // remove first asterisk on each line, all whitespace before that asterisk,
    // and one whitespace behind that asterik
    Pattern pattern = Pattern.compile("^\\s*\\*\\s?(.*)$", Pattern.MULTILINE);
    String jsDocWithoutAsterisks =
        pattern.matcher(jsDocWithoutOpeningAndClosingTag).replaceAll("$1");
    LOGGER.trace("jsdoc2: {}", jsDocWithoutAsterisks);
    return jsDocWithoutAsterisks;
  }

  /**
   * Include statements and type declarations must be removed from scripts in order to process them
   * with the Rhino JavaScript parser, otherwise the following error will occur:
   * org.mozilla.javascript.EvaluatorException: missing ; before statement
   * 
   * <p>From
   * https://www.sparxsystems.com/enterprise_architect_user_guide/15.2/automation/script_editors.html:
   * 
   * <p>Intelli-sense is available not only in the 'Script Editor', but also in the 'Script
   * Console'; Intelli-sense at its most basic is presented for the inbuilt functionality of the
   * script engine.
   * 
   * <p>For Intelli-sense on the additional Enterprise Architect scripting objects (as listed) you
   * must declare variables according to syntax that specifies a type; it is not necessary to use
   * this syntax to execute a script properly, it is only present so that the correct Intelli-sense
   * can be displayed for an item.
   * 
   * <p>The syntax can be seen in, for example:
   * 
   * <p>Dim e as EA.Element
   * 
   * <p>[...]
   * 
   * <p>An Include statement (!INC) allows a script to reference constants, functions and variables
   * defined by another script accessible within the Scripting Window. Include statements are
   * typically used at the beginning of a script.
   * 
   * <p>o include a script library, use this syntax:
   * 
   * <p>!INC [Script Group Name].[Script Name]
   */
  private String removeEaSpecificConstructsFromScriptContents(String scriptContents) {
    String scriptWithoutEaSpecificConstructs =
        scriptContents.replaceAll("!INC .*", "").replaceAll(" as EA[^;]*", "");
    return scriptWithoutEaSpecificConstructs;
  }

  private Parser getParser() {
    CompilerEnvirons env = new CompilerEnvirons();
    env.setRecordingLocalJsDocComments(true);
    env.setRecordingComments(true);
    env.setRecoverFromErrors(true);
    env.setStrictMode(false);
    env.setLanguageVersion(Context.VERSION_ES6);
    return new Parser(env);
  }
}
