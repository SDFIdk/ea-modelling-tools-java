package dk.gov.data.modellingtools.exception;

/**
 * High-level exception used by these tools. Low-lever exceptions and throwables can be wrapped in
 * this one.
 */
public class ModellingToolsException extends Exception {

  private static final long serialVersionUID = -8970215711043543511L;

  public ModellingToolsException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModellingToolsException(String message) {
    super(message);
  }

}
