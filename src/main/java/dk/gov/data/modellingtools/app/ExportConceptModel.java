package dk.gov.data.modellingtools.app;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.conceptmodel.ConceptModelExporter;
import dk.gov.data.modellingtools.export.conceptmodel.impl.ConceptModelExporterImpl;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Exports a concept model modelled according to the FDA UML profile.
 */
public class ExportConceptModel extends AbstractApplication {

  /**
   * Entry point.
   */
  public static void main(String[] args) {
    new ExportConceptModel().run(args);
  }

  @Override
  protected String getDescription() {
    return "export concepts to file system; different file formats are supported";
  }

  @Override
  protected void doApplicationSpecificLogic(CommandLine commandLine,
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, ModellingToolsException {
    String format = commandLine.getOptionValue(AbstractApplication.OPTION_OUTPUT_FORMAT);
    ConceptModelExporter conceptModelExporter = new ConceptModelExporterImpl(eaWrapper);
    Validate.isTrue(
        format
            .matches("(" + StringUtils.join(conceptModelExporter.getSupportedFormats(), "|") + ")"),
        "Unsupported format " + format + ". Supported formats are: "
            + StringUtils.join(conceptModelExporter.getSupportedFormats(), ", "));
    String packageGuid = commandLine.getOptionValue(AbstractApplication.OPTION_PACKAGE);
    File folder = (File) commandLine.getParsedOptionValue(AbstractApplication.OPTION_OUTPUT_FOLDER);

    conceptModelExporter.exportConceptModel(packageGuid, folder, format);
  }

  @Override
  protected void createOptions() {
    super.createOptions();
    addOptionOutputFolder();
    addOptionPackage();
    addOptionOutputFormat();
  }

}
