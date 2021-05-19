package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.model.Concept;
import dk.gov.data.modellingtools.utils.UuidUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Element;
import org.sparx.Package;

/**
 * Implementation based on the FDA rules for concept and data modelling.
 */
public class ConceptDaoFda implements ConceptDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConceptDaoFda.class);

  private static final String STEREOTYPE_FDA_CONCEPT = "FDAprofil::Concept";

  @Override
  public List<Concept> findAll(Package umlPackage) {
    List<Concept> concepts = new ArrayList<>();
    for (Element element : umlPackage.GetElements()) {
      if (STEREOTYPE_FDA_CONCEPT.equals(element.GetFQStereotype())) {
        Concept concept = new Concept();

        concept.setUuid(UuidUtils.standardizeUuidNotation(element.GetElementGUID()));

        Map<String, String> preferredTerms = new HashMap<>();
        preferredTerms.put("da", TaggedValueUtils.getTaggedValueValue(element, "prefLabel (da)"));
        preferredTerms.put("en", TaggedValueUtils.getTaggedValueValue(element, "prefLabel (en)"));
        concept.setPreferredTerms(preferredTerms);

        Map<String, String> definitions = new HashMap<>();
        definitions.put("da", TaggedValueUtils.getTaggedValueValue(element, "definition (da)"));
        definitions.put("en", TaggedValueUtils.getTaggedValueValue(element, "definition (en)"));
        concept.setDefinitions(definitions);

        concepts.add(concept);
      } else {
        LOGGER.debug("Ignoring element " + element.GetName()
            + " because it does not have stereotype " + STEREOTYPE_FDA_CONCEPT);
      }
    }
    return concepts;
  }

}
