package dk.gov.data.modellingtools.utils;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltExecutable;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.XmlStreamReader;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

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
    Serializer serializer = getSaxonProcessor().newSerializer(writer);
    transformXml(xml, stylesheetResourceName, serializer);
  }

  /**
   * Transforms the given xml using the given stylesheet and writes it to the given file.
   *
   * @param stylesheetResourceName must start with a slash and consist of resource folder (or config
   *        folder) and file name, concatenated with /
   */
  public static void transformXml(String xml, String stylesheetResourceName, File outputFile)
      throws ModellingToolsException {
    Serializer serializer = getSaxonProcessor().newSerializer(outputFile);
    transformXml(xml, stylesheetResourceName, serializer);
  }

  /**
   * Transforms the given xml using the given stylesheet and writes it using the given
   * {@link Serializer}.
   *
   * @param stylesheetResourceName must start with a slash and consist of resource folder (or config
   *        folder) and file name, concatenated with /
   */
  private static void transformXml(String xml, String stylesheetResourceName, Serializer serializer)
      throws ModellingToolsException {
    Validate.isTrue(stylesheetResourceName.startsWith("/"),
        "Stylesheet name must start with slash (/), see also javadoc of java.lang.Class.getResourceAsStream(String name)");
    try {
      Source source = createSourceFromXml(xml);
      InputStream inputStreamXsl =
          XmlAndXsltUtils.class.getResourceAsStream(stylesheetResourceName);
      XsltExecutable stylesheet =
          getSaxonProcessor().newXsltCompiler().compile(new StreamSource(inputStreamXsl));
      Xslt30Transformer xslt30Transformer = stylesheet.load30();
      xslt30Transformer.transform(source, serializer);
    } catch (IOException e) {
      throw new ModellingToolsException("An exception occurred while processing an XML string", e);
    } catch (SaxonApiException e) {
      throw new ModellingToolsException(
          "An exception occurred while using stylesheet " + stylesheetResourceName, e);
    }
  }

  /**
   * Creates a {@link Source}, encoded in UTF-8, from the given XML formatted string.
   * 
   * <p>If the first character of the string is a <a href
   * ="https://en.wikipedia.org/wiki/Byte_order_mark">byte order mark</a>, it is removed before
   * creating the {@link Source}. Other Xerces would give a {@link SAXParseException} with message
   * "Content is not allowed in prolog".
   * 
   * <p>If an XML declaration is present with an encoding stated, that encoding declaration is then
   * changed to "UTF-8", as otherwise an error would occur (from the XML specification: it is a
   * fatal error for an entity including an encoding declaration to be presented to the XML
   * processor in an encoding other than that named in the declaration).</p>
   *
   * @see <a
   *      href="https://www.joelonsoftware.com/2003/10/08/the-absolute-minimum-every-software-developer-absolutely-positively-must-know-about-unicode-and-character-sets-no-excuses/">The
   *      Absolute Minimum Every Software Developer Absolutely, Positively Must Know About Unicode
   *      and Character Sets (No Excuses!)</a>
   * @see <a href="https://www.w3.org/TR/xml/">Extensible Markup Language (XML) 1.0</a>
   */
  private static Source createSourceFromXml(String xml) throws IOException {
    Validate.notNull(xml, "The XML string must not be mull");
    return new StreamSource(IOUtils.toInputStream(
        changeDeclaredEncodingToUtf8IfPresent(removeBomIfPresent(xml)), Charset.forName("UTF-8")));
  }

  private static String changeDeclaredEncodingToUtf8IfPresent(String xmlWithoutBom) {
    final String xmlDeclaredAsUtf8;
    if (xmlWithoutBom.startsWith("<?xml")) {
      String xmlDeclaration = xmlWithoutBom.substring(0, xmlWithoutBom.indexOf("?>") + 2);
      LOGGER.debug("XML declaration: {}", xmlDeclaration);
      final Matcher m = XmlStreamReader.ENCODING_PATTERN.matcher(xmlDeclaration);
      if (m.find()) {
        final String encodingWithQuotes = m.group(1).toUpperCase(Locale.ROOT);
        // use same kind of quotes
        char quote = encodingWithQuotes.charAt(0);
        xmlDeclaredAsUtf8 = xmlWithoutBom.replaceFirst(encodingWithQuotes, quote + "UTF-8" + quote);
      } else {
        xmlDeclaredAsUtf8 = xmlWithoutBom;
      }
    } else {
      xmlDeclaredAsUtf8 = xmlWithoutBom;
    }
    return xmlDeclaredAsUtf8;
  }

  /**
   * Finds the encoding declaration in the XML prolog and converts it to a {@link Charset}.
   * 
   * <p>Example: the encoding in &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-16&quot;
   * standalone=&quot;no&quot; ?&gt; is declared to be UTF-16.</p>
   */
  public static Charset findCharsetDeclaredInXml(final String xmlWithoutBom) {
    final String encoding;
    if (xmlWithoutBom.startsWith("<?xml")) {
      String xmlDeclaration = xmlWithoutBom.substring(0, xmlWithoutBom.indexOf("?>") + 2);
      LOGGER.debug("XML declaration: {}", xmlDeclaration);
      final Matcher m = XmlStreamReader.ENCODING_PATTERN.matcher(xmlDeclaration);
      if (m.find()) {
        final String encodingWithQuotes = m.group(1).toUpperCase(Locale.ROOT);
        encoding = encodingWithQuotes.substring(1, encodingWithQuotes.length() - 1);
      } else {
        encoding = "UTF-8";
      }
    } else {
      encoding = "UTF-8";
    }
    return Charset.forName(encoding);
  }

  /**
   * Removes the byte order mark, if it is present in the beginning of the given XML string.
   */
  private static String removeBomIfPresent(String xml) {
    final String xmlWithoutBom;
    if (xml.charAt(0) == ByteOrderMark.UTF_BOM) {
      xmlWithoutBom = xml.substring(1);
    } else {
      xmlWithoutBom = xml;
    }
    return xmlWithoutBom;
  }

  /**
   * Convert the given XML formatted string to a node according to the XQuery and XPath Data Model
   * (XDM) data model.
   *
   * @see <a href="https://www.w3.org/TR/xpath-datamodel/">XQuery and XPath Data Model 3.1</a>
   * @see <a href="https://www.saxonica.com/documentation12/index.html#!sourcedocs/tree-models">XML
   *      tree models</a>
   */
  public static XdmNode createNodeFromXmlFormattedString(String xmlFormattedString)
      throws ModellingToolsException {
    try {
      DocumentBuilder documentBuilder = getSaxonProcessor().newDocumentBuilder();
      XdmNode queryResultAsXdmNode = documentBuilder.build(createSourceFromXml(xmlFormattedString));
      return queryResultAsXdmNode;
    } catch (IOException e) {
      LOGGER.info(xmlFormattedString);
      throw new ModellingToolsException("An exception occurred while processing an XML string", e);
    } catch (SaxonApiException e) {
      LOGGER.info(xmlFormattedString);
      throw new ModellingToolsException(
          "An exception occurred while trying to deal with an SQL query result from EA: "
              + e.getMessage(),
          e);
    }
  }

  private static Processor getSaxonProcessor() {
    synchronized (XmlAndXsltUtils.class) {
      if (processor == null) {
        processor = new Processor(false);
      }
    }
    return processor;
  }
}
