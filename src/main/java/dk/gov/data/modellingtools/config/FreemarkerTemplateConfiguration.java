package dk.gov.data.modellingtools.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.util.Locale;

/**
 * Gives access to the Freemarker configuration.
 * 
 * <p>This class is a Singleton implemented using an enum, see also
 * https://dzone.com/articles/java-singletons-using-enum.</p>
 */
public enum FreemarkerTemplateConfiguration {

  INSTANCE;

  private final Configuration configuration;

  FreemarkerTemplateConfiguration() {
    configuration = new Configuration(Configuration.VERSION_2_3_32);
    String encoding = "UTF-8";
    configuration.setTemplateLoader(new ResourceStreamTemplateLoader("/templates", encoding));
    configuration.setDefaultEncoding(encoding);
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    configuration.setRecognizeStandardFileExtensions(true);
    // UNDetermined language
    configuration.setLocale(Locale.forLanguageTag("und"));
    configuration.setLocalizedLookup(true);
    configuration.setLogTemplateExceptions(false);
  }

  /**
   * Global point of access to the Freemarker configuration from anywhere in the code.
   */
  public Configuration getConfiguration() {
    return (Configuration) configuration.clone();
  }



}
