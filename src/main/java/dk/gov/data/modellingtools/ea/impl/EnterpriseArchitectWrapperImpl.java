package dk.gov.data.modellingtools.ea.impl;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.DataModellingToolsException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;
import org.sparx.Repository;
import org.sparx.Services;

/**
 * Implementation of the wrapper for Enterprise Architect.
 */
public class EnterpriseArchitectWrapperImpl implements EnterpriseArchitectWrapper {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(EnterpriseArchitectWrapperImpl.class);

  private static final String TASKLIST_EA = "TASKLIST /V /FO CSV /NH /FI \"IMAGENAME eq EA.exe\"";

  private Repository eaRepository;

  public EnterpriseArchitectWrapperImpl(int eaProcessId) throws DataModellingToolsException {
    super();
    this.eaRepository = retrieveRepository(eaProcessId);
  }

  private Repository retrieveRepository(int eaProcessId) throws DataModellingToolsException {
    if (OS.isFamilyWindows()) {
      String output = queryTaskListForEaProcesses(eaProcessId);
      validateOutput(eaProcessId, output);
      return Services.GetRepository(eaProcessId);
    } else {
      throw new DataModellingToolsException(
          "This application can currently only be used in Windows");
    }
  }

  private String queryTaskListForEaProcesses() throws DataModellingToolsException {
    String taskListCommand = TASKLIST_EA;
    return executeTaskListCommand(taskListCommand);
  }

  private String queryTaskListForEaProcesses(int eaProcessId) throws DataModellingToolsException {
    String taskListCommand = TASKLIST_EA + " /FI \"PID eq " + eaProcessId + "\"";
    return executeTaskListCommand(taskListCommand);
  }

  private String executeTaskListCommand(String taskListCommand) throws DataModellingToolsException {
    try (ByteArrayOutputStream normalAndErrorOutputStream = new ByteArrayOutputStream()) {
      CommandLine cmdLine = CommandLine.parse(taskListCommand);
      DefaultExecutor executor = new DefaultExecutor();
      executor.setStreamHandler(new PumpStreamHandler(normalAndErrorOutputStream));
      int exitValue = executor.execute(cmdLine);
      LOGGER.debug("Exit value " + exitValue + " for " + taskListCommand);
      String output = normalAndErrorOutputStream.toString(Charset.defaultCharset());
      LOGGER.debug(output);
      return output;
    } catch (IOException e) {
      throw new DataModellingToolsException(
          "An error occurred when trying to execute " + taskListCommand, e);
    }
  }

  private void validateOutput(int eaProcessId, String output) throws DataModellingToolsException {
    if (output.indexOf("No tasks are running which match the specified criteria") != -1) {
      LOGGER.info("Output received:\r\n" + output);
      LOGGER.info("All running EA processes: ");
      String outputAllEaProcesses = queryTaskListForEaProcesses();
      LOGGER.info("\r\n" + outputAllEaProcesses);
      throw new DataModellingToolsException("No EA process found with pid " + eaProcessId
          + ", check the previous logging output to see the currently running EA processes");
    }
  }

  @Override
  public void writeToScriptWindow(String message) throws DataModellingToolsException {
    /*
     * 1st parameter: specifies the tab on which to display the text; 2nd parameter: specifies the
     * text to display; 3rd parameter: specifies a numeric ID value to associate with this output
     * item for further handling by Add-Ins; can be set to 0 if no handling is required
     */
    this.eaRepository.WriteOutput("Script", message, 0);
  }

  @Override
  public String sqlQuery(String query) throws DataModellingToolsException {
    return this.eaRepository.SQLQuery(query);
  }

  @Override
  public Package getPackageByGuid(String packageGuid) throws DataModellingToolsException {
    return this.eaRepository.GetPackageByGuid(packageGuid);
  }

}
