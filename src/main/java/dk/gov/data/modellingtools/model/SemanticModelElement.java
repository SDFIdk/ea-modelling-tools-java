package dk.gov.data.modellingtools.model;

/**
 * Model element with a concept connected to it.
 */
public class SemanticModelElement extends ModelElement {

  private Concept concept;

  public Concept getConcept() {
    return concept;
  }

  public void setConcept(Concept concept) {
    this.concept = concept;
  }

}
