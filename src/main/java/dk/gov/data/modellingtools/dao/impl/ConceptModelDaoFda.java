package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.ConceptModelDao;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.model.ConceptModel;
import org.apache.commons.lang3.Validate;
import org.sparx.Package;

/**
 * Implementation based on the FDA rules for concept and data modelling.
 */
public class ConceptModelDaoFda implements ConceptModelDao {

  private static final String STEREOTYPE_FDA_CONCEPT_MODEL = "FDAprofil::ConceptModel";

  @Override
  public ConceptModel findByPackage(Package umlPackage) {
    String fqStereotype = umlPackage.GetElement().GetFQStereotype();
    Validate.isTrue(STEREOTYPE_FDA_CONCEPT_MODEL.equals(fqStereotype),
        "Stereotype must be " + STEREOTYPE_FDA_CONCEPT_MODEL + " but is " + fqStereotype);
    ConceptModel conceptModel = new ConceptModel();
    conceptModel.setName(umlPackage.GetName());
    conceptModel.setDescription(TaggedValueUtils.getTaggedValueValue(umlPackage, "comment (da)"));
    return conceptModel;
  }

}
