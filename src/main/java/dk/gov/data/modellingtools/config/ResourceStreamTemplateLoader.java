package dk.gov.data.modellingtools.config;

import freemarker.cache.TemplateLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link TemplateLoader} that uses an {@link InputStream} from a resource as template source.
 */
class ResourceStreamTemplateLoader implements TemplateLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStreamTemplateLoader.class);

  private final String basePath;

  private final String encoding;

  public ResourceStreamTemplateLoader(String basePath, String encoding) {
    super();
    Validate.notNull(basePath, "A base path (the folder name) must be given");
    Validate.isTrue(basePath.startsWith("/"),
        "An absolute path must be given. See also method java.lang.Class.getResourceAsStream(String)");
    this.basePath = StringUtils.removeEnd(basePath, "/");
    this.encoding = encoding;
  }

  /**
   * Returns an {@link InputStream} as template source.
   */
  @Override
  public Object findTemplateSource(String name) throws IOException {
    String absoluteName = basePath + "/" + name;
    LOGGER.debug("Trying to find template resource with absolute name {}", absoluteName);
    InputStream resourceAsStream = this.getClass().getResourceAsStream(absoluteName);
    if (resourceAsStream == null) {
      LOGGER.info("Resource with absolute name {} does not exist on classpath: {}", absoluteName,
          System.getProperty("java.class.path"));
    }
    return resourceAsStream;
  }

  @Override
  public long getLastModified(Object templateSource) {
    return -1;
  }

  @Override
  public Reader getReader(Object templateSource, String encoding) throws IOException {
    return new InputStreamReader((InputStream) templateSource, this.encoding);
  }

  @Override
  public void closeTemplateSource(Object templateSource) throws IOException {
    ((InputStream) templateSource).close();
  }

}
