package dk.gov.data.geo.datamodellingtools.app;

import dk.gov.data.geo.datamodellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.geo.datamodellingtools.exception.DataModellingToolsException;
import dk.gov.data.geo.datamodellingtools.export.conceptmodel.ConceptModelExporter;
import dk.gov.data.geo.datamodellingtools.export.conceptmodel.impl.ConceptModelExporterImpl;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportConceptModel extends AbstractApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExportConceptModel.class);

  /**
   * Entry point.
   */
  public static void main(String[] args) {
    LOGGER.info("Starting java code in " + System.getProperty("user.dir"));
    try {
      new ExportConceptModel().run(args);
      LOGGER.info("Finished");
    } catch (DataModellingToolsException e) {
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
  protected String getDescription() {
    return "export concepts to file system; different file formats are supported";
  }

  @Override
  protected void doApplicationSpecificLogic(CommandLine commandLine,
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, DataModellingToolsException {
    String format = commandLine.getOptionValue(AbstractApplication.OPTION_OUTPUT_FORMAT);
    ConceptModelExporter conceptModelExporter = new ConceptModelExporterImpl(eaWrapper);
    Validate.isTrue(
        format
            .matches("(" + StringUtils.join(conceptModelExporter.getSupportedFormats(), "|") + ")"),
        "Unsupported format " + format + ". Supported formats are: "
            + StringUtils.join(conceptModelExporter.getSupportedFormats(), ", "));
    String packageGuid = commandLine.getOptionValue(AbstractApplication.OPTION_PACKAGE);
    File folder = (File) commandLine.getParsedOptionValue(AbstractApplication.OPTION_OUTPUT_FOLDER);

    conceptModelExporter.exportConceptModel(packageGuid, folder, format,
        getTemplateConfiguration());
  }

  @Override
  protected void createOptions() {
    super.createOptions();
    addOptionOutputFolder();
    addOptionPackage();
    addOptionOutputFormat();
  }

}
