package dk.gov.data.modellingtools.ea.utils;

import java.util.Map;

/**
 * Utility methods for creating maps containing language strings.
 */
public final class MapUtils {

  private MapUtils() {}

  /**
   * Creates a map containing two key-value pairs.
   * 
   * <ol> <li>"da" and the first parameter</li> <li>"en" and the second parameter</li> </ol>
   */
  public static Map<String, String> mapLanguageAndValue(String danishText, String englishText) {
    return Map.of("da", danishText, "en", englishText);
  }

  /**
   * Creates a map containing two key-value pairs.
   * 
   * <ol> <li>"da" and the first parameter</li> <li>"en" and the second parameter</li> </ol>
   */
  public static Map<String, String[]> mapLanguageAndValue(String[] danishTexts,
      String[] englishTexts) {
    return Map.of("da", danishTexts, "en", englishTexts);
  }

}
