package dk.gov.data.modellingtools.utils;

public class FormatUtils {

  public static String getFileFormatExtension(String format) {
    String outputFileExtension;
    switch (format) {
      case "asciidoc":
        outputFileExtension = ".adoc";
        break;
      case "rdf":
        outputFileExtension = ".rdf";
        break;
      default:
        throw new IllegalArgumentException("Unknown format " + format);
    }
    return outputFileExtension;
  }

  /**
   * @see https://freemarker.apache.org/docs/pgui_config_outputformatsautoesc.html
   */
  public static String getTemplateExtension(String format) {
    String templateExtension;
    switch (format) {
      case "asciidoc":
        templateExtension = ".ftl";
        break;
      case "rdf":
        templateExtension = ".ftlx";
        break;
      default:
        throw new IllegalArgumentException("Unknown format " + format);
    }
    return templateExtension;
  }

}
