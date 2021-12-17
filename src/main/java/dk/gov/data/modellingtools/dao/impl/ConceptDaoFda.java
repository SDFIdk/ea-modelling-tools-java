package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
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

  // TODO make configurable
  private static final char SPLIT_CHARACTER = ';';

  @Override
  public List<Concept> findAll(Package umlPackage) throws ModellingToolsException {
    List<Concept> concepts = new ArrayList<>();
    for (Element element : umlPackage.GetElements()) {
      if (STEREOTYPE_FDA_CONCEPT.equals(element.GetFQStereotype())) {
        // TODO refactor this code a bit, a lot of copy-paste
        Concept concept = new Concept();

        concept.setIdentifier(TaggedValueUtils.getTaggedValueValueAsUri(element, "URI"));

        Map<String, String> preferredTerms = new HashMap<>();
        preferredTerms.put("da", TaggedValueUtils.getTaggedValueValue(element, "prefLabel (da)"));
        preferredTerms.put("en", TaggedValueUtils.getTaggedValueValue(element, "prefLabel (en)"));
        concept.setPreferredTerms(preferredTerms);

        Map<String, String[]> acceptedTerms = new HashMap<>();
        acceptedTerms.put("da", StringUtils.stripAll(StringUtils.split(
            TaggedValueUtils.getTaggedValueValue(element, "altLabel (da)"), SPLIT_CHARACTER)));
        acceptedTerms.put("en", StringUtils.stripAll(StringUtils.split(
            TaggedValueUtils.getTaggedValueValue(element, "altLabel (en)"), SPLIT_CHARACTER)));
        concept.setAcceptedTerms(acceptedTerms);

        Map<String, String[]> deprecatedTerms = new HashMap<>();
        deprecatedTerms.put("da",
            StringUtils.stripAll(StringUtils.split(
                TaggedValueUtils.getTaggedValueValue(element, "deprecatedLabel (da)"),
                SPLIT_CHARACTER)));
        deprecatedTerms.put("en",
            StringUtils.stripAll(StringUtils.split(
                TaggedValueUtils.getTaggedValueValue(element, "deprecatedLabel (en)"),
                SPLIT_CHARACTER)));
        concept.setDeprecatedTerms(deprecatedTerms);

        Map<String, String> definitions = new HashMap<>();
        definitions.put("da", TaggedValueUtils.getTaggedValueValue(element, "definition (da)"));
        definitions.put("en", TaggedValueUtils.getTaggedValueValue(element, "definition (en)"));
        concept.setDefinitions(definitions);

        Map<String, String> examples = new HashMap<>();
        examples.put("da", TaggedValueUtils.getTaggedValueValue(element, "example (da)"));
        examples.put("en", TaggedValueUtils.getTaggedValueValue(element, "example (en)"));
        concept.setExamples(examples);

        Map<String, String> notes = new HashMap<>();
        notes.put("da", TaggedValueUtils.getTaggedValueValue(element, "comment (da)"));
        notes.put("en", TaggedValueUtils.getTaggedValueValue(element, "comment (en)"));
        concept.setNotes(notes);

        Map<String, String> applicationNotes = new HashMap<>();
        applicationNotes.put("da",
            TaggedValueUtils.getTaggedValueValue(element, "applicationNote (da)"));
        applicationNotes.put("en",
            TaggedValueUtils.getTaggedValueValue(element, "applicationNote (en)"));
        concept.setApplicationNotes(applicationNotes);

        concept.setSource(TaggedValueUtils.getTaggedValueValueAsUri(element, "source"));

        String taggedValueName = "seeAlso";
        String[] additionalInformationResourcesAsStrings = StringUtils.stripAll(StringUtils.split(
            TaggedValueUtils.getTaggedValueValue(element, taggedValueName), SPLIT_CHARACTER));
        URI[] additionalInformationResources =
            new URI[additionalInformationResourcesAsStrings.length];
        for (int i = 0; i < additionalInformationResourcesAsStrings.length; i++) {
          try {
            additionalInformationResources[i] = new URI(additionalInformationResourcesAsStrings[i]);
          } catch (URISyntaxException e) {
            throw new ModellingToolsException("Could not convert string "
                + additionalInformationResourcesAsStrings[i] + ", a part of tagged value "
                + taggedValueName + " " + " on element " + element.GetName() + " to a valid URI",
                e);
          }
        }
        concept.setAdditionalInformationResources(additionalInformationResources);

        concept.setDefiningConceptModel(
            TaggedValueUtils.getTaggedValueValueAsUri(element, "isDefinedBy"));

        // TODO add broader concept



        concepts.add(concept);

        // sort alphabetically on the Danish preferred term
        concepts.sort(new Comparator<Concept>() {

          @Override
          public int compare(Concept c1, Concept c2) {
            return c1.getPreferredTerms().get("da").compareTo(c2.getPreferredTerms().get("da"));
          }
        });
      } else {
        LOGGER.debug("Ignoring element " + element.GetName()
            + " because it does not have stereotype " + STEREOTYPE_FDA_CONCEPT);
      }
    }
    return concepts;
  }

}
