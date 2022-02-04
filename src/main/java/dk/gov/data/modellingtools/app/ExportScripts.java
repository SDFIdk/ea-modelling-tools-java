package dk.gov.data.modellingtools.app;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.scriptmanagement.ScriptManager;
import dk.gov.data.modellingtools.scriptmanagement.impl.ScriptManagerImpl;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exports the scripts from an Enterprise Architect file.
 */
public class ExportScripts extends AbstractApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExportScripts.class);

  private static final String OPTION_SCRIPT_GROUP = "sg";

  private static final String OPTION_DOCUMENTATION = "doc";

  public ExportScripts() {
    super();
  }

  /**
   * Entry point.
   */
  public static void main(String... args) {
    LOGGER.info("Starting java code in " + System.getProperty("user.dir"));
    try {
      new ExportScripts().run(args);
      LOGGER.info("Finished");
    } catch (ModellingToolsException e) {
      LOGGER.debug(e.getMessage(), e);
      LOGGER.error("Error: " + e.getMessage());
    } catch (UnsatisfiedLinkError e) {
      if (e.getMessage().contains("SSJavaCOM64")) {
        LOGGER.error("A Java 32-bit must be used for dealing with EA models", e);
      } else {
        LOGGER.error("Unexpected error: " + e.getMessage(), e);
      }
      LOGGER.error("Unexpected error: " + e.getMessage(), e);
    } catch (Throwable e) {
      LOGGER.error("Unexpected error: " + e.getMessage(), e);
    }
  }

  @Override
  protected void createOptions() {
    super.createOptions();
    addOptionOutputFolder();
    addOptionExportScripts();
    addOptionCreateScriptDocumentation();
  }

  private void addOptionExportScripts() {
    options.addOption(Option.builder(OPTION_SCRIPT_GROUP).longOpt("script-group").hasArg()
        .argName("script group (regex)").required()
        .desc("script group(s) to export (required). Wildcards can be used, note that * (asterisk)"
            + " must be escaped with a backslash: \\*")
        .build());
  }

  private void addOptionCreateScriptDocumentation() {
    options.addOption(
        Option.builder(OPTION_DOCUMENTATION).longOpt("create-documentation").hasArg(false)
            .desc("Extracts the documentation from the scripts that are exported, and saves "
                + "it in one file, README.md (optional).")
            .build());
  }

  @Override
  protected void doApplicationSpecificLogic(CommandLine commandLine,
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, ModellingToolsException {
    String scriptGroupNameOrRegex = retrieveScriptGroupNameOrRegex(commandLine);
    File folder = (File) commandLine.getParsedOptionValue(AbstractApplication.OPTION_OUTPUT_FOLDER);
    boolean createDocumentation = commandLine.hasOption(OPTION_DOCUMENTATION);

    ScriptManager scriptManager = new ScriptManagerImpl(eaWrapper, getTemplateConfiguration());
    scriptManager.exportScripts(scriptGroupNameOrRegex, folder, createDocumentation);
  }

  /**
   * <p> Oracle and OpenJDK runtimes expand * (asterisk) to a list of files.So the asterisk must be
   * escaped when calling Main, and Main must unescape the asterisk. </p>
   * 
   * <p> See
   * https://stackoverflow.com/questions/6577524/how-to-expand-java-classpath-wildcards-from- code
   * and https://docs.oracle.com/en/java/javase/11/tools/java.html#GUID-3B1CE181-CD30-4178-9602-
   * 230B800D4FAE </p>
   * 
   * 
   */
  private String retrieveScriptGroupNameOrRegex(CommandLine commandLine) {
    String scriptGroupNameOrRegexFromCl = commandLine.getOptionValue(OPTION_SCRIPT_GROUP);
    String scriptGroupNameOrRegexForScriptManager =
        StringUtils.replace(scriptGroupNameOrRegexFromCl, "\\*", "*");
    return scriptGroupNameOrRegexForScriptManager;
  }

  @Override
  protected String getDescription() {
    return "export scripts to file system and create reference data for import in EA";
  }

}
