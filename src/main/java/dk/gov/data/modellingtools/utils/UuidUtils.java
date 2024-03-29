package dk.gov.data.modellingtools.utils;

import java.util.Locale;
import java.util.UUID;

/**
 * Utilities method that have something to do with Enterprise Architect.
 */
public class UuidUtils {

  /**
   * Sets the UUID and validates the syntax.
   *
   * @throws IllegalArgumentException If the uuid does not conform to the string representation as
   *         described in {@link UUID#toString()}
   */
  public static String standardizeUuidNotation(String guid) {
    String uuid = guid.toLowerCase(Locale.ENGLISH).replace("{", "").replace("}", "");
    return UUID.fromString(uuid).toString();
  }

}
