package dk.gov.data.modellingtools.utils;

/**
 * Utilities for file format.
 */
public class FileFormatUtils {

  /**
   * Get the extension that should be use for the given file format.
   */
  public static String getFileFormatExtension(String fileFormat) {
    String outputFileExtension;
    switch (fileFormat) {
      case "asciidoc":
        outputFileExtension = ".adoc";
        break;
      case "rdf":
        outputFileExtension = ".rdf";
        break;
      case "csv":
        outputFileExtension = ".csv";
        break;
      default:
        throw new IllegalArgumentException("Unknown format " + fileFormat);
    }
    return outputFileExtension;
  }

  /**
   * Get extension for the template file, based on the file format.
   * 
   * <p>See also https://freemarker.apache.org/docs/pgui_config_outputformatsautoesc.html.</p>
   */
  public static String getTemplateExtension(String fileFormat) {
    String templateExtension;
    switch (fileFormat) {
      case "asciidoc":
      case "csv":
        templateExtension = ".ftl";
        break;
      case "rdf":
        templateExtension = ".ftlx";
        break;
      default:
        throw new IllegalArgumentException("Unknown format " + fileFormat);
    }
    return templateExtension;
  }

}