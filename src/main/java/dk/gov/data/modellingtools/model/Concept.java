package dk.gov.data.modellingtools.model;

import java.net.URI;
import java.util.Map;
import org.apache.commons.exec.util.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Concept that has the properties described in ISO 1087. See also
 * https://www.iso.org/obp/ui#iso:std:iso:1087:ed-2:v1:en.
 * 
 * <p> The key of all the maps is the language code (e.g. "da" or "en"). </p>
 *
 */
public class Concept {

  private URI identifier;

  private URI[] additionalInformationResources;

  private URI definingConceptModel;

  private Map<String, String> preferredTerms;

  private Map<String, String[]> acceptedTerms;

  private Map<String, String[]> deprecatedTerms;

  private Map<String, String> definitions;

  private Map<String, String> examples;

  private Map<String, String> notes;

  // 0..* in FDA profile
  private URI source;

  private String sourceTextualReference;

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

  public URI[] getAdditionalInformationResources() {
    return ArrayUtils.clone(additionalInformationResources);
  }

  public void setAdditionalInformationResources(URI[] additionalInformationResources) {
    this.additionalInformationResources = ArrayUtils.clone(additionalInformationResources);
  }

  public URI getDefiningConceptModel() {
    return definingConceptModel;
  }

  public void setDefiningConceptModel(URI definingConceptModel) {
    this.definingConceptModel = definingConceptModel;
  }

  public Map<String, String> getPreferredTerms() {
    return MapUtils.copy(preferredTerms);
  }

  public void setPreferredTerms(Map<String, String> preferredTerms) {
    this.preferredTerms = MapUtils.copy(preferredTerms);
  }

  public Map<String, String[]> getAcceptedTerms() {
    return MapUtils.copy(acceptedTerms);
  }

  public void setAcceptedTerms(Map<String, String[]> acceptedTerms) {
    this.acceptedTerms = MapUtils.copy(acceptedTerms);
  }

  public Map<String, String[]> getDeprecatedTerms() {
    return MapUtils.copy(deprecatedTerms);
  }

  public void setDeprecatedTerms(Map<String, String[]> deprecatedTerms) {
    this.deprecatedTerms = MapUtils.copy(deprecatedTerms);
  }

  public Map<String, String> getDefinitions() {
    return MapUtils.copy(definitions);
  }

  public void setDefinitions(Map<String, String> definitions) {
    this.definitions = MapUtils.copy(definitions);
  }

  public Map<String, String> getExamples() {
    return MapUtils.copy(examples);
  }

  public void setExamples(Map<String, String> examples) {
    this.examples = MapUtils.copy(examples);
  }

  public Map<String, String> getNotes() {
    return MapUtils.copy(notes);
  }

  public void setNotes(Map<String, String> notes) {
    this.notes = MapUtils.copy(notes);
  }

  public URI getSource() {
    return source;
  }

  public void setSource(URI source) {
    this.source = source;
  }

  public String getSourceTextualReference() {
    return sourceTextualReference;
  }

  public void setSourceTextualReference(String sourceTextualReference) {
    this.sourceTextualReference = sourceTextualReference;
  }


}
