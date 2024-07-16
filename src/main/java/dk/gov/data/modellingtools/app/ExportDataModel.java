package dk.gov.data.modellingtools.app;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.datamodel.DataModelExporter;
import dk.gov.data.modellingtools.export.datamodel.impl.DataModelExporterImpl;
import java.io.File;
import java.util.Locale;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Exports a data model.
 */
public class ExportDataModel extends AbstractApplication {

  public static void main(String... args) {
    new ExportDataModel().run(args);
  }

  @Override
  protected String getDescription() {
    return "export the documentation of the elements of a data model to a file";
  }

  @Override
  protected void doApplicationSpecificLogic(CommandLine commandLine,
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, ModellingToolsException {
    String packageGuid = commandLine.getOptionValue(OPTION_PACKAGE);
    File folder = (File) commandLine.getParsedOptionValue(OPTION_OUTPUT_FOLDER);
    String format = commandLine.getOptionValue(OPTION_OUTPUT_FORMAT);
    String language = commandLine.getOptionValue(OPTION_LANGUAGE);
    Locale locale = retrieveLocale(language);
    String strictnessModeString =
        commandLine.getOptionValue(OPTION_STRICTNESS_MODE, StrictnessMode.STRICT.toString());
    StrictnessMode strictnessMode = retrieveStrictnessMode(strictnessModeString);

    DataModelExporter dataModelExporter = new DataModelExporterImpl(eaWrapper);
    Validate.isTrue(
        format.matches("(" + StringUtils.join(dataModelExporter.getSupportedFormats(), "|") + ")"),
        "Unsupported format " + format + ". Supported formats are: "
            + StringUtils.join(dataModelExporter.getSupportedFormats(), ", "));
    dataModelExporter.exportDataModel(packageGuid, folder, format, locale, strictnessMode);
  }

  @Override
  protected void createOptions() {
    super.createOptions();
    addOptionOutputFolder();
    addOptionPackage();
    addOptionOutputFormat();
    addOptionLanguage();
    addOptionStrictnessMode();
  }

}
