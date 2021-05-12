package dk.gov.data.geo.datamodellingtools.model;

import java.util.Map;

/**
 * Concept that has the properties described in ISO 1087. See also
 * https://www.iso.org/obp/ui#iso:std:iso:1087:ed-2:v1:en.
 * 
 * <p>
 * The key of all the maps is the language code (e.g. "da" or "en").
 * </p>
 *
 */
public class Concept {

  private String uuid;
  private Map<String, String> preferredTerms;
  private Map<String, String> definitions;

  /**
   * Default constructor.
   */
  public Concept() {
    super();
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Map<String, String> getPreferredTerms() {
    return preferredTerms;
  }

  public void setPreferredTerms(Map<String, String> preferredTerms) {
    this.preferredTerms = preferredTerms;
  }

  public Map<String, String> getDefinitions() {
    return definitions;
  }

  public void setDefinitions(Map<String, String> definitions) {
    this.definitions = definitions;
  }


}
