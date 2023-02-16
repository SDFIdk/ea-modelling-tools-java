package dk.gov.data.modellingtools.app;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.projectmanagement.ProjectTransferer;
import dk.gov.data.modellingtools.projectmanagement.impl.ProjectTransfererImpl;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transfer EA projects to other formats.
 */
public class TransferProjects extends AbstractApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferProjects.class);

  @Override
  protected String getDescription() {
    return "transfers projects to other, file based repositories";
  }

  @Override
  protected void createOptions() {
    // no EA process id needed here, so not calling super.createOptions()
    options = new Options();
    addOptionInputFolder();
    addOptionInputFormat();
    addOptionOutputFormat();
  }

  @Override
  protected void doApplicationSpecificLogic(CommandLine commandLine,
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, ModellingToolsException {
    File inputFolder =
        (File) commandLine.getParsedOptionValue(AbstractApplication.OPTION_INPUT_FOLDER);
    Validate.notNull(inputFolder, "The input folder must not be null");
    Validate.isTrue(inputFolder.exists(), "The input folder, %s, must exist",
        inputFolder.getAbsolutePath());
    Validate.isTrue(inputFolder.isDirectory(), "The input folder must be a folder",
        inputFolder.getAbsolutePath());

    String inputFormat = commandLine.getOptionValue(AbstractApplication.OPTION_INPUT_FORMAT);
    String outputFormat = commandLine.getOptionValue(AbstractApplication.OPTION_OUTPUT_FORMAT);

    LOGGER.info("Starting project transfer from format {} to format {} for input folder {}",
        inputFormat, outputFormat, inputFolder.getAbsolutePath());

    ProjectTransferer projectTransferer = new ProjectTransfererImpl(eaWrapper);
    projectTransferer.transferProjects(inputFolder, inputFormat, outputFormat);
  }

  /**
   * Entry point.
   */
  public static void main(String[] args) {
    new TransferProjects().run(args);
  }

}
