package dk.gov.data.modellingtools.model;

import java.net.URI;
import java.util.Map;

/**
 * Concept that has the properties described in ISO 1087. See also
 * https://www.iso.org/obp/ui#iso:std:iso:1087:ed-2:v1:en.
 * 
 * <p> The key of all the maps is the language code (e.g. "da" or "en"). </p>
 *
 */
public class Concept {

  private URI identifier;

  private Map<String, String> preferredTerms;
  private Map<String, String[]> acceptedTerms;
  private Map<String, String[]> deprecatedTerms;
  private Map<String, String> definitions;
  private Map<String, String> examples;
  private Map<String, String> notes;
  private Map<String, String> applicationNotes;

  private URI source;
  private URI[] additionalInformationResources;
  private URI definingConceptModel;

  // TODO add broader concept


  /**
   * Default constructor.
   */
  public Concept() {
    super();
  }

  public URI getIdentifier() {
    return identifier;
  }

  public void setIdentifier(URI identifier) {
    this.identifier = identifier;
  }

  public Map<String, String> getPreferredTerms() {
    return preferredTerms;
  }

  public void setPreferredTerms(Map<String, String> preferredTerms) {
    this.preferredTerms = preferredTerms;
  }

  public Map<String, String[]> getAcceptedTerms() {
    return acceptedTerms;
  }

  public void setAcceptedTerms(Map<String, String[]> acceptedTerms) {
    this.acceptedTerms = acceptedTerms;
  }

  public Map<String, String[]> getDeprecatedTerms() {
    return deprecatedTerms;
  }

  public void setDeprecatedTerms(Map<String, String[]> deprecatedTerms) {
    this.deprecatedTerms = deprecatedTerms;
  }

  public Map<String, String> getDefinitions() {
    return definitions;
  }

  public void setDefinitions(Map<String, String> definitions) {
    this.definitions = definitions;
  }

  public Map<String, String> getExamples() {
    return examples;
  }

  public void setExamples(Map<String, String> examples) {
    this.examples = examples;
  }

  public Map<String, String> getNotes() {
    return notes;
  }

  public void setNotes(Map<String, String> notes) {
    this.notes = notes;
  }

  public Map<String, String> getApplicationNotes() {
    return applicationNotes;
  }

  public void setApplicationNotes(Map<String, String> applicationNotes) {
    this.applicationNotes = applicationNotes;
  }

  public URI getSource() {
    return source;
  }

  public void setSource(URI source) {
    this.source = source;
  }

  public URI[] getAdditionalInformationResources() {
    return additionalInformationResources;
  }

  public void setAdditionalInformationResources(URI[] additionalInformationResources) {
    this.additionalInformationResources = additionalInformationResources;
  }

  public URI getDefiningConceptModel() {
    return definingConceptModel;
  }

  public void setDefiningConceptModel(URI definingConceptModel) {
    this.definingConceptModel = definingConceptModel;
  }

}
