package dk.gov.data.modellingtools.app;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.update.datamodel.DataModelTagsUpdater;
import dk.gov.data.modellingtools.update.datamodel.impl.DataModelTagsUpdaterImpl;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.Validate;

/**
 * Imports a data model.
 */
public class UpdateDataModelTags extends AbstractApplication {

  private static final String OPTION_TAG_UPDATE_MODE = "tum";

  public static void main(String... args) {
    new UpdateDataModelTags().run(args);
  }

  @Override
  protected String getDescription() {
    return "imports characteristics of elements of a data model from a file";
  }

  @Override
  protected void doApplicationSpecificLogic(CommandLine commandLine,
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, ModellingToolsException {
    String packageGuid = commandLine.getOptionValue(OPTION_PACKAGE);
    File inputFile = (File) commandLine.getParsedOptionValue(OPTION_INPUT_FILE);
    Validate.isTrue(inputFile.isFile(), "The file with path %1$s is not a normal file",
        inputFile.getAbsolutePath());
    String tagUpdateModeString = commandLine.getOptionValue(OPTION_TAG_UPDATE_MODE);
    TagUpdateMode tagUpdateMode = retrieveTagUpdateMode(tagUpdateModeString);

    DataModelTagsUpdater dataModelUpdater = new DataModelTagsUpdaterImpl(eaWrapper);
    dataModelUpdater.updateDataModel(packageGuid, inputFile, tagUpdateMode);
  }

  @Override
  protected void createOptions() {
    super.createOptions();
    addOptionPackage();
    addOptionInputFile();
    addOptionTagUpdateMode();
  }

  /**
   * Adds an option for the tag update mode.
   */
  private void addOptionTagUpdateMode() {
    options.addOption(Option.builder(OPTION_TAG_UPDATE_MODE).longOpt("tag-update-mode").hasArg()
        .argName("tag update mode").type(String.class).desc("'update_only' or 'update_add_delete'")
        .required().build());
  }

  protected TagUpdateMode retrieveTagUpdateMode(String tagUpdateModeString) {
    try {
      TagUpdateMode tagUpdateMode =
          TagUpdateMode.valueOf(tagUpdateModeString.toUpperCase(Locale.ENGLISH));
      return tagUpdateMode;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Expected one of the following values (case-insensitive): "
          + Arrays.toString(TagUpdateMode.values()), e);
    }
  }


}
