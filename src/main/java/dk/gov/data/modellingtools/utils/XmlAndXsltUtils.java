package dk.gov.data.modellingtools.utils;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.scriptmanagement.impl.ScriptManagerImpl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltExecutable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for dealing with XML and XSLT.
 */
public class XmlAndXsltUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(XmlAndXsltUtils.class);

  static {
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
        "Stylesheet name must start with slash (/), see also javadoc of java.lang.Class.getResourceAsStream(String name)");
    try (InputStream inputStreamXml = IOUtils.toInputStream(xml, StandardCharsets.UTF_8);
        InputStream inputStreamXsl =
            XmlAndXsltUtils.class.getResourceAsStream(stylesheetResourceName);) {
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
        "Stylesheet name must start with slash (/), see also javadoc of java.lang.Class.getResourceAsStream(String name)");
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

  private static Processor getProcessor() {
    synchronized (XmlAndXsltUtils.class) {
      if (processor == null) {
        processor = new Processor(false);
      }
    }
    return processor;
  }

  /**
   * Assumes UTF-8.
   */
  public static XdmNode createNodeFromXmlFormattedString(String xmlFormattedString)
      throws ModellingToolsException {
    try {
      DocumentBuilder documentBuilder = getProcessor().newDocumentBuilder();
      XdmNode queryResultAsXdmNode = documentBuilder.build(
          new StreamSource(IOUtils.toInputStream(xmlFormattedString, StandardCharsets.UTF_8)));
      return queryResultAsXdmNode;
    } catch (SaxonApiException e) {
      LOGGER.info(xmlFormattedString);
      throw new ModellingToolsException(
          "An exception occurred while trying to deal with an SQL query result from EA: "
              + e.getMessage(),
          e);
    }
  }

}
