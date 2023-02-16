package dk.gov.data.modellingtools.app;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.impl.EnterpriseArchitectWrapperImpl;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.logging.EnterpriseArchitectScriptWindowAppender;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.Iterator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application that other applications in this package should inherit from.
 */
public abstract class AbstractApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApplication.class);

  private static final String ENV_VARIABLE_LOCATION_TOOLS = "EAMT_HOME";

  public static final String OPTION_EA_PROCESS_ID = "eapid";
  public static final String OPTION_PAUSE = "p";
  public static final String OPTION_INPUT_FOLDER = "i";
  public static final String OPTION_OUTPUT_FOLDER = "o";
  public static final String OPTION_PACKAGE = "pkg";
  public static final String OPTION_INPUT_FORMAT = "f";
  public static final String OPTION_OUTPUT_FORMAT = "t";
  public static final String OPTION_LANGUAGE = "l";


  private HelpFormatter helpFormatter;
  protected Options options;

  private boolean shouldEaBeTerminated;

  public AbstractApplication() {
    super();
  }

  protected final void addCommonOptions() {
    options.addOption(Option.builder(AbstractApplication.OPTION_EA_PROCESS_ID)
        .longOpt("ea-process-id").hasArg().argName("EA process id").required()
        .desc("process id of the running EA instance (required)").build());
    /*
     * Pause option: The script in EA closes the command line window as soon as the Java application
     * has finished running. Sometimes we want to study the output in that window.
     */
    options.addOption(Option.builder(AbstractApplication.OPTION_PAUSE).longOpt("pause-at-end")
        .hasArg().argName("seconds")
        .desc("number of seconds this application should be paused at the end"
            + " (useful when calling Java from EA) (optional)")
        .build());
  }

  protected void createOptions() {
    options = new Options();
    addCommonOptions();
  }

  /**
   * Adds a required option for an input folder.
   */
  protected final void addOptionInputFolder() {
    options.addOption(Option.builder(AbstractApplication.OPTION_INPUT_FOLDER)
        .longOpt("input-folder").hasArg().argName("folder path").argName("folder").type(File.class)
        .required().desc("specifies the input folder path (required)").build());
  }

  /**
   * Adds a required option for an output folder.
   */
  protected final void addOptionOutputFolder() {
    options.addOption(Option.builder(AbstractApplication.OPTION_OUTPUT_FOLDER)
        .longOpt("output-folder").hasArg().argName("folder path").argName("folder").type(File.class)
        .required().desc("specifies the output folder path for the output (required)").build());
  }

  /**
   * Adds a required option for a package.
   */
  protected final void addOptionPackage() {
    options.addOption(Option.builder(AbstractApplication.OPTION_PACKAGE).longOpt("package-guid")
        .hasArg().argName("UML package GUID").type(String.class).required()
        .desc("GUID of the package in the model").build());
  }

  /**
   * Adds a required option for an input format.
   */
  protected final void addOptionInputFormat() {
    options.addOption(Option.builder(AbstractApplication.OPTION_INPUT_FORMAT).longOpt("from-format")
        .hasArg().argName("format name").type(String.class).desc("input file format").required()
        .build());
  }

  /**
   * Adds a required option for an output format.
   */
  protected final void addOptionOutputFormat() {
    options.addOption(Option.builder(AbstractApplication.OPTION_OUTPUT_FORMAT).longOpt("to-format")
        .hasArg().argName("format name").type(String.class).desc("file format to be exported to")
        .required().build());
  }

  /**
   * Adds an optional option for a language.
   */
  protected final void addOptionLanguage() {
    options.addOption(Option.builder(AbstractApplication.OPTION_LANGUAGE).longOpt("language")
        .hasArg().argName("2-letter language code").type(String.class)
        .desc("language for the output").required(false).build());
  }

  private HelpFormatter getHelpFormatter() {
    if (helpFormatter == null) {
      helpFormatter = new HelpFormatter();
      helpFormatter.setWidth(100);
    }
    return helpFormatter;
  }

  protected abstract String getDescription();

  protected String getApplicationName() {
    String applicationName;
    if (System.getProperty("app.name") == null) {
      // in an IDE
      applicationName = this.getClass().getSimpleName();
    } else {
      // in the jar file
      applicationName = System.getProperty("app.name");
    }
    return applicationName;
  }

  private String getCommandLineSyntax() {
    String commandLineSyntax;
    if (System.getProperty("app.name") == null) {
      commandLineSyntax = this.getClass().getName();
    } else {
      commandLineSyntax =
          "%" + ENV_VARIABLE_LOCATION_TOOLS + "%\\bin\\" + System.getProperty("app.name") + ".bat";
    }
    return commandLineSyntax;
  }

  /**
   * Print the help message to System.out.
   */
  protected void printHelp(Options options) {
    getHelpFormatter().printHelp(getCommandLineSyntax(), getDescription(), options, null);
  }

  /**
   * Creates the options for the application, runs the application and handles exceptions and
   * throwables.
   */
  @SuppressFBWarnings("DCN_NULLPOINTER_EXCEPTION")
  protected final void run(String... args) {
    StopWatch stopWatch = StopWatch.createStarted();
    try {
      LOGGER.info("Starting java code in {}", System.getProperty("user.dir"));
      createOptions();
      if (args == null || args.length == 0) {
        printHelp(options);
      } else {
        parseArgumentsAndRunApplication(args);
      }
    } catch (ModellingToolsException e) {
      LOGGER.error("Error: {}", e.getMessage(), e);
    } catch (IllegalArgumentException e) {
      LOGGER.error("A method has been passed an illegal or inappropriate argument: {}",
          e.getMessage(), e);
    } catch (NullPointerException e) {
      // org.apache.commons.lang3.Validate.notNull throws a NullPointerException
      LOGGER.error("A method has been passed a null argument: {}", e.getMessage(), e);
    } catch (Throwable e) {
      LOGGER.error("Unexpected error: {}", e.getMessage(), e);
    } finally {
      stopWatch.stop();
      LOGGER.info("Finished in {}", stopWatch.formatTime());
    }
  }

  private void parseArgumentsAndRunApplication(String... args) throws ModellingToolsException {
    CommandLine commandLine = null;
    EnterpriseArchitectWrapper eaWrapper = null;
    try {
      commandLine = new DefaultParser().parse(options, args);
      eaWrapper = createEaWrapper(commandLine);

      startEaScriptWindowAppenders(eaWrapper);
      doApplicationSpecificLogic(commandLine, eaWrapper);
    } catch (ParseException e) {
      printHelp(options);
      throw new ModellingToolsException(
          "Could not parse the following arguments according to the options set for "
              + getApplicationName() + ": " + StringUtils.join(args, ' '),
          e);
    } catch (NumberFormatException e) {
      printHelp(options);
      throw new ModellingToolsException(
          "Could not parse an argument from the command line to a string", e);
    } finally {
      if (commandLine == null) {
        pauseApplication(15);
      } else {
        pauseApplicationIfRequested(commandLine);
      }
      if (eaWrapper != null && shouldEaBeTerminated) {
        eaWrapper.terminate();
      }
    }
  }

  private EnterpriseArchitectWrapper createEaWrapper(CommandLine commandLine)
      throws ModellingToolsException {
    String eaProcessIdFromCommandLine =
        commandLine.getOptionValue(AbstractApplication.OPTION_EA_PROCESS_ID);
    EnterpriseArchitectWrapper eaWrapper;
    if (eaProcessIdFromCommandLine == null) {
      eaWrapper = new EnterpriseArchitectWrapperImpl();
      shouldEaBeTerminated = true;
    } else {
      int eaProcessId = Integer.parseInt(eaProcessIdFromCommandLine);
      eaWrapper = new EnterpriseArchitectWrapperImpl(eaProcessId);
      shouldEaBeTerminated = false;
    }
    return eaWrapper;
  }

  private void pauseApplicationIfRequested(CommandLine commandLine) throws ModellingToolsException {
    if (commandLine.hasOption(AbstractApplication.OPTION_PAUSE)) {
      long seconds = Long.parseLong(commandLine.getOptionValue(AbstractApplication.OPTION_PAUSE));
      pauseApplication(seconds);
    }
  }

  private void pauseApplication(long seconds) throws ModellingToolsException {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      throw new ModellingToolsException("Could not pause application at the end", e);
    }
  }

  /**
   * Starts any appenders of type {@link EnterpriseArchitectScriptWindowAppender}.
   */
  private void startEaScriptWindowAppenders(EnterpriseArchitectWrapper eaWrapper) {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    for (ch.qos.logback.classic.Logger logger : loggerContext.getLoggerList()) {
      for (Iterator<Appender<ILoggingEvent>> i = logger.iteratorForAppenders(); i.hasNext();) {
        Appender<ILoggingEvent> appender = i.next();
        if (appender instanceof EnterpriseArchitectScriptWindowAppender) {
          EnterpriseArchitectScriptWindowAppender<ILoggingEvent> eaAppender =
              (EnterpriseArchitectScriptWindowAppender<ILoggingEvent>) appender;
          eaAppender.setEnterpriseArchitectWrapper(eaWrapper);
          eaAppender.start();
        }
      }
    }
  }

  protected abstract void doApplicationSpecificLogic(CommandLine commandLine,
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, ModellingToolsException;

}
