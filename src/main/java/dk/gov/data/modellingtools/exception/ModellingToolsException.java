package dk.gov.data.modellingtools.exception;

public class ModellingToolsException extends Exception {

  private static final long serialVersionUID = -8970215711043543511L;

  public ModellingToolsException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModellingToolsException(String message) {
    super(message);
  }

}
