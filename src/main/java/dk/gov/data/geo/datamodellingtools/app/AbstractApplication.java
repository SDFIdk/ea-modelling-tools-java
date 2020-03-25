package dk.gov.data.geo.datamodellingtools.app;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import dk.gov.data.geo.datamodellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.geo.datamodellingtools.ea.impl.EnterpriseArchitectWrapperImpl;
import dk.gov.data.geo.datamodellingtools.exception.DataModellingToolsException;
import dk.gov.data.geo.datamodellingtools.logging.EnterpriseArchitectScriptWindowAppender;
import java.io.File;
import java.util.Iterator;
import net.sf.saxon.lib.NamespaceConstant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApplication.class);

  private static final String ENV_VARIABLE_LOCATION_TOOLS = "DMT_HOME";

  public static final String OPTION_EA_PROCESS_ID = "eapid";
  public static final String OPTION_PAUSE = "p";
  public static final String OPTION_OUTPUT_FOLDER = "o";

  private HelpFormatter helpFormatter;
  protected Options options;

  public AbstractApplication() {
    super();
    setSystemPropertiesForXmlProcessing();
  }

  private void setSystemPropertiesForXmlProcessing() {
    // dependency xerces:xercesImpl
    System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
        "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
    // dependency net.sf.saxon:Saxon-HE
    System.setProperty("javax.xml.transform.TransformerFactory",
        "net.sf.saxon.TransformerFactoryImpl");
    // dependency net.sf.saxon:Saxon-HE
    System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_SAXON,
        "net.sf.saxon.xpath.XPathFactoryImpl");
  }

  protected final void addCommonOptions() {
    options.addOption(Option.builder(AbstractApplication.OPTION_EA_PROCESS_ID)
        .longOpt("ea-process-id").hasArg().argName("EA process id").required()
        .desc("process id of the running EA instance").build());
    /*
     * Pause option: The script in EA closes the command line window as soon as the Java application
     * has finished running. Sometimes we want to study the output in that window.
     */
    options.addOption(Option.builder(AbstractApplication.OPTION_PAUSE).longOpt("pause-at-end")
        .hasArg().argName("seconds")
        .desc("number of seconds this application should be paused at the end"
            + " (useful when calling Java from EA)")
        .build());
  }

  protected void createOptions() {
    options = new Options();
    addCommonOptions();
  }

  protected final void addOptionOutputFolder() {
    options.addOption(Option.builder(AbstractApplication.OPTION_OUTPUT_FOLDER)
        .longOpt("output-folder").hasArg().argName("folder path").argName("folder").type(File.class)
        .desc("specifies the output folder path for the output").build());
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
      applicationName = this.getClass().getSimpleName();
    } else {
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

  protected final void run(String... args) throws DataModellingToolsException {
    createOptions();
    if (args == null || args.length == 0) {
      printHelp(options);
    } else {
      try {
        CommandLine commandLine = new DefaultParser().parse(options, args);
        int eaProcessId =
            Integer.parseInt(commandLine.getOptionValue(AbstractApplication.OPTION_EA_PROCESS_ID));
        EnterpriseArchitectWrapper eaWrapper = new EnterpriseArchitectWrapperImpl(eaProcessId);

        startEaScriptWindowAppenders(eaWrapper);

        doApplicationSpecificLogic(commandLine, eaWrapper);

        if (commandLine.hasOption(AbstractApplication.OPTION_PAUSE)) {
          long seconds =
              Long.parseLong(commandLine.getOptionValue(AbstractApplication.OPTION_PAUSE));
          Thread.sleep(seconds * 1000);
        }
      } catch (ParseException e) {
        printHelp(options);
        LOGGER.debug(e.getMessage(), e);
        throw new DataModellingToolsException(
            "Could not parse the following arguments according to the options set for "
                + getApplicationName() + ": " + StringUtils.join(args, ' '));
      } catch (NumberFormatException e) {
        printHelp(options);
        LOGGER.debug(e.getMessage(), e);
        throw new DataModellingToolsException(
            "Could not parse an argument from the command line to a string");
      } catch (InterruptedException e) {
        LOGGER.debug(e.getMessage(), e);
        throw new DataModellingToolsException("Could not pause application at the end");
      }
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
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, DataModellingToolsException;

}
