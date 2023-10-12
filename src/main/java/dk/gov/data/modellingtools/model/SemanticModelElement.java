package dk.gov.data.modellingtools.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Model element with a concept connected to it.
 */
@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
public class SemanticModelElement extends ModelElement {

  private Concept concept;

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Concept getConcept() {
    return concept;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setConcept(Concept concept) {
    this.concept = concept;
  }

}
