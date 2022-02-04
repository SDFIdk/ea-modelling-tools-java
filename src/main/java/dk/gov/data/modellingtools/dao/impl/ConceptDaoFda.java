package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.ConnectorEnd;
import org.sparx.Element;
import org.sparx.Package;

/**
 * Implementation based on the FDA rules for concept and data modelling.
 */
public class ConceptDaoFda implements ConceptDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConceptDaoFda.class);

  // TODO make configurable
  private static final char SPLIT_CHARACTER = ';';

  @Override
  public List<Concept> findAll(Package umlPackage) throws ModellingToolsException {
    List<Concept> concepts = new ArrayList<>();
    for (Element element : umlPackage.GetElements()) {
      if (FdaConstants.FQ_STEREOTYPE_CONCEPT.equals(element.GetFQStereotype())) {
        concepts.add(findByElement(element));
      } else {
        LOGGER.debug("Ignoring {} because it does not have stereotype {}",
            EaModelUtils.toString(element), FdaConstants.FQ_STEREOTYPE_CONCEPT);
      }
    }
    return concepts;
  }

  Concept findByElement(Element element) throws ModellingToolsException {
    LOGGER.debug("Finding concept for {}", EaModelUtils.toString(element));
    return findByObject(element);
  }

  Concept findByAttribute(Attribute attribute) throws ModellingToolsException {
    LOGGER.debug("Finding concept for {}", EaModelUtils.toString(attribute));
    return findByObject(attribute);
  }

  Concept findByConnectorEnd(ConnectorEnd connectorEnd) throws ModellingToolsException {
    LOGGER.debug("Finding concept for {}", EaModelUtils.toString(connectorEnd));
    return findByObject(connectorEnd);
  }

  private Concept findByObject(Object object) throws ModellingToolsException {
    // TODO refactor this code a bit, a lot of copy-paste
    Concept concept = new Concept();
    Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(object);
    Validate.isTrue(taggedValues.keySet().containsAll(FdaConstants.getAllModelElementTags()),
        EaModelUtils.toString(object)
            + " does not contain all FDA tags, synchronize the stereotypes in your EA model. "
            + "\r\nFDA tags: " + StringUtils.join(FdaConstants.getAllModelElementTags()
                + "; found tags: " + StringUtils.join(taggedValues.keySet())));

    try {
      concept.setIdentifier(new URI(taggedValues.get(FdaConstants.TAG_URI)));
    } catch (URISyntaxException e) {
      String identifier = taggedValues.get(FdaConstants.TAG_URI);
      LOGGER.warn("Could not convert {} on {} to a valid URI, ignoring message {}", identifier,
          EaModelUtils.toString(object), e.getMessage());
    }

    Map<String, String> preferredTerms = new HashMap<>();
    preferredTerms.put("da", taggedValues.get(FdaConstants.TAG_PREFERRED_LABEL_DA));
    preferredTerms.put("en", taggedValues.get(FdaConstants.TAG_PREFERRED_LABEL_EN));
    concept.setPreferredTerms(preferredTerms);

    Map<String, String[]> acceptedTerms = new HashMap<>();
    acceptedTerms.put("da", TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
        FdaConstants.TAG_ALTERNATIVE_LABEL_DA, SPLIT_CHARACTER));
    acceptedTerms.put("en", TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
        FdaConstants.TAG_ALTERNATIVE_LABEL_EN, SPLIT_CHARACTER));
    concept.setAcceptedTerms(acceptedTerms);

    Map<String, String[]> deprecatedTerms = new HashMap<>();
    deprecatedTerms.put("da", TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
        FdaConstants.TAG_DEPRECATED_LABEL_DA, SPLIT_CHARACTER));
    deprecatedTerms.put("en", TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
        FdaConstants.TAG_DEPRECATED_LABEL_DA, SPLIT_CHARACTER));
    concept.setDeprecatedTerms(deprecatedTerms);

    Map<String, String> definitions = new HashMap<>();
    definitions.put("da", taggedValues.get(FdaConstants.TAG_DEFINITION_DA));
    definitions.put("en", taggedValues.get(FdaConstants.TAG_DEFINITION_EN));
    concept.setDefinitions(definitions);

    Map<String, String> examples = new HashMap<>();
    examples.put("da", taggedValues.get(FdaConstants.TAG_EXAMPLE_DA));
    examples.put("en", taggedValues.get(FdaConstants.TAG_EXAMPLE_EN));
    concept.setExamples(examples);

    Map<String, String> notes = new HashMap<>();
    notes.put("da", taggedValues.get(FdaConstants.TAG_COMMENT_DA));
    notes.put("en", taggedValues.get(FdaConstants.TAG_COMMENT_EN));
    concept.setNotes(notes);

    String sourceTaggedValue = taggedValues.get(FdaConstants.TAG_SOURCE);
    if (StringUtils.isNotBlank(sourceTaggedValue)) {
      try {
        URI sourceUri = new URI(sourceTaggedValue);
        if ("https".equals(sourceUri.getScheme()) || "http".equals(sourceUri.getScheme())) {
          concept.setSource(sourceUri);
        } else {
          LOGGER.warn(
              "Could not convert {} on {} to an absolute HTTP-URI, treating it as a textual reference",
              sourceTaggedValue, EaModelUtils.toString(object));
          concept.setSourceTextualReference(sourceTaggedValue);
        }
      } catch (URISyntaxException e) {
        LOGGER.warn(
            "Could not convert {} on {} to a valid URI, ignoring message {}, treating it as a textual reference",
            sourceTaggedValue, EaModelUtils.toString(object), e.getMessage());
        concept.setSourceTextualReference(sourceTaggedValue);
      }
    }
    // tag not defined in FDA profile
    String taggedValueName = "seeAlso";
    String taggedValueValue = taggedValues.get(taggedValueName);
    if (taggedValueValue != null) {
      String[] additionalInformationResourcesAsStrings = TaggedValueUtils
          .retrieveAndSplitTaggedValueValue(taggedValues, taggedValueName, SPLIT_CHARACTER);
      URI[] additionalInformationResources =
          new URI[additionalInformationResourcesAsStrings.length];
      for (int i = 0; i < additionalInformationResourcesAsStrings.length; i++) {
        try {
          additionalInformationResources[i] = new URI(additionalInformationResourcesAsStrings[i]);
        } catch (URISyntaxException e) {
          LOGGER.warn("Could not convert {} on {} to a valid URI, ignoring message {}",
              additionalInformationResourcesAsStrings[i], EaModelUtils.toString(object),
              e.getMessage());
        }
      }
      concept.setAdditionalInformationResources(additionalInformationResources);
    }

    try {
      concept.setDefiningConceptModel(new URI(taggedValues.get(FdaConstants.TAG_IS_DEFINED_BY)));
    } catch (URISyntaxException e) {
      String definingConceptModelUri = taggedValues.get(FdaConstants.TAG_IS_DEFINED_BY);
      LOGGER.info("Could not convert {} to a valid URI, ignoring message {}",
          definingConceptModelUri, e.getMessage());
    }

    // TODO add broader concept
    return concept;
  }

}
