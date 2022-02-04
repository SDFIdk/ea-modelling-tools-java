package dk.gov.data.modellingtools.app;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.vocabulary.VocabularyExporter;
import dk.gov.data.modellingtools.export.vocabulary.impl.VocabularyExporterImpl;
import java.io.File;
import java.net.URL;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exports a data model to a vocabulary.
 */
public class ExportDataModelVocabulary extends AbstractApplication {

  private static final String OPTION_HEADER = "h";
  private static final String OPTION_METADATA = "m";
  private static final Logger LOGGER = LoggerFactory.getLogger(ExportDataModelVocabulary.class);

  /**
   * Entry point.
   */
  public static void main(String[] args) {
    LOGGER.info("Starting java code in {}", System.getProperty("user.dir"));
    try {
      new ExportDataModelVocabulary().run(args);
      LOGGER.info("Finished");
    } catch (ModellingToolsException e) {
      LOGGER.debug(e.getMessage(), e);
      LOGGER.error("Error: {}", e.getMessage());
    } catch (UnsatisfiedLinkError e) {
      if (e.getMessage().contains("SSJavaCOM64")) {
        LOGGER.error("A Java 32-bit must be used for dealing with EA models", e);
      } else {
        LOGGER.error("Unexpected error: {}", e.getMessage(), e);
      }
      LOGGER.error("Unexpected error: {}", e.getMessage(), e);
    } catch (Throwable e) {
      LOGGER.error("Unexpected error: {}", e.getMessage(), e);
    }
  }

  @Override
  protected String getDescription() {
    return "export a data model to a vocabulary (= terminological dictionary which contains designations and definitions from one or more specific subject fields)";
  }

  @Override
  protected void doApplicationSpecificLogic(CommandLine commandLine,
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, ModellingToolsException {
    String packageGuid = commandLine.getOptionValue(AbstractApplication.OPTION_PACKAGE);
    File folder = (File) commandLine.getParsedOptionValue(AbstractApplication.OPTION_OUTPUT_FOLDER);
    String format = commandLine.getOptionValue(AbstractApplication.OPTION_OUTPUT_FORMAT);
    String language = commandLine.getOptionValue(AbstractApplication.OPTION_LANGUAGE, "da");
    Boolean hasHeader = commandLine.hasOption(OPTION_HEADER);
    Boolean hasMetadata = commandLine.hasOption(OPTION_METADATA);
    URL metadataUrl = null;
    if (hasMetadata) {
      metadataUrl = (URL) commandLine.getParsedOptionValue(OPTION_METADATA);
    }

    VocabularyExporter vocabularyExporter =
        new VocabularyExporterImpl(eaWrapper, getTemplateConfiguration());
    Validate.isTrue(
        format.matches("(" + StringUtils.join(vocabularyExporter.getSupportedFormats(), "|") + ")"),
        "Unsupported format " + format + ". Supported formats are: "
            + StringUtils.join(vocabularyExporter.getSupportedFormats(), ", "));
    vocabularyExporter.exportVocabulary(packageGuid, folder, format, language, hasHeader,
        hasMetadata, metadataUrl);
  }

  @Override
  protected void createOptions() {
    super.createOptions();
    addOptionOutputFolder();
    addOptionPackage();
    addOptionOutputFormat();
    addOptionLanguage();
    options.addOption(Option.builder(OPTION_HEADER).longOpt("header").hasArg(false).required(false)
        .type(Boolean.class).desc("a header should be added to the file").build());
    options.addOption(Option.builder(OPTION_METADATA).longOpt("metadata").hasArg().argName("URL")
        .numberOfArgs(1).required(false).type(URL.class).desc("Metadata link").build());
  }

}
