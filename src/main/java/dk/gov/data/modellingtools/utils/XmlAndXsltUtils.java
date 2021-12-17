package dk.gov.data.modellingtools.utils;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Diagram;
import dk.gov.data.modellingtools.scriptmanagement.impl.ScriptManagerImpl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltExecutable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

public class XmlAndXsltUtils {

  private static Processor processor;

  /**
   * Transforms the given xml using the given stylesheet and writes it to a string.
   * 
   * @param stylesheetResourceName must start with / and consist of resource folder (or
   *        configfolder) and file name, concatenated with /
   */
  public static String transformXml(String xml, String stylesheetResourceName)
      throws ModellingToolsException {
    StringWriter writer = new StringWriter();
    transformXml(xml, stylesheetResourceName, writer);
    return writer.toString();
  }

  /**
   * Transforms the given xml using the given stylesheet and writes it to the given writer.
   * 
   * @param stylesheetResourceName must start with / and consist of resource folder (or
   *        configfolder) and file name, concatenated with /
   */
  public static void transformXml(String xml, String stylesheetResourceName, Writer writer)
      throws ModellingToolsException {
    Validate.isTrue(stylesheetResourceName.startsWith("/"),
        "stylesheetAbsoluteName must start with /, see also javadoc of java.lang.Class.getResourceAsStream(String name)");
    try (InputStream inputStreamXml = IOUtils.toInputStream(xml, StandardCharsets.UTF_8);
        InputStream inputStreamXsl = Diagram.class.getResourceAsStream(stylesheetResourceName);) {
      XsltExecutable stylesheet =
          getProcessor().newXsltCompiler().compile(new StreamSource(inputStreamXsl));
      Serializer serializer = getProcessor().newSerializer(writer);
      Xslt30Transformer transformer = stylesheet.load30();
      transformer.transform(new StreamSource(inputStreamXml), serializer);
    } catch (IOException | SaxonApiException e) {
      throw new ModellingToolsException(
          "An exception occurred while using stylesheet " + stylesheetResourceName, e);
    }
  }

  /**
   * Transforms the given xml using the given stylesheet and writes it to the given file.
   * 
   * @param stylesheetResourceName must start with a slash and consist of resource folder (or config
   *        folder) and file name, concatenated with another slash
   */
  public static void transformXml(String xml, String stylesheetResourceName, File outputFile)
      throws ModellingToolsException {
    Validate.isTrue(stylesheetResourceName.startsWith("/"),
        "stylesheetAbsoluteName must start with a slash, see also javadoc of java.lang.Class.getResourceAsStream(String name)");
    try (InputStream inputStreamXml = IOUtils.toInputStream(xml, StandardCharsets.UTF_8);
        InputStream inputStreamXsl =
            ScriptManagerImpl.class.getResourceAsStream(stylesheetResourceName);) {
      XsltExecutable stylesheet =
          getProcessor().newXsltCompiler().compile(new StreamSource(inputStreamXsl));
      Serializer serializer = getProcessor().newSerializer(outputFile);
      Xslt30Transformer transformer = stylesheet.load30();
      transformer.transform(new StreamSource(inputStreamXml), serializer);
    } catch (IOException | SaxonApiException e) {
      throw new ModellingToolsException(
          "An exception occurred while using stylesheet " + stylesheetResourceName, e);
    }
  }

  public static Processor getProcessor() {
    if (processor == null) {
      processor = new Processor(false);
    }
    return processor;
  }

}
