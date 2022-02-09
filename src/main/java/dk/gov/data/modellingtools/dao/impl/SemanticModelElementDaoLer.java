package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

/**
 * Workaround until the LER data model is updated to follow the FDA profile. The LER data model
 * contains special tags that are not part of any profile to record the terms.
 */
public class SemanticModelElementDaoLer extends AbstractSemanticModelElementDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(SemanticModelElementDaoLer.class);

  public SemanticModelElementDaoLer(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Element element) {
    return element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_OBJECT_TYPE)
        || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_FEATURE_TYPE)
        || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_ENUMERATION)
        || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_CODE_LIST)
        || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_DATA_TYPE);
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    return attributeFqStereotypes.containsMapping(attribute.GetAttributeGUID(),
        BasicData1Constants.FQ_STEREOTYPE_PROPERTY);
  }

  @Override
  protected boolean qualifiesOppositeEndAsSemanticModelElement(
      MultiValuedMap<String, String> connectorEndFqStereotypes, EaConnectorEnd eaConnectorEnd) {
    return connectorEndFqStereotypes.containsMapping(eaConnectorEnd.getConnectorEndUniqueId(),
        BasicData1Constants.FQ_STEREOTYPE_PROPERTY);
  }

  private List<String> getNecessaryTags() {
    return Arrays.asList(BasicData1Constants.TAG_DEFINITION, BasicData1Constants.TAG_EXAMPLE,
        BasicData1Constants.TAG_NOTE);
  }

  @Override
  protected Concept createConcept(Element element) throws ModellingToolsException {
    return createConceptGeneral(element);
  }

  @Override
  protected Concept createConcept(Attribute attribute) throws ModellingToolsException {
    return createConceptGeneral(attribute);
  }

  @Override
  protected Concept createConcept(ConnectorEnd connectorEnd) throws ModellingToolsException {
    return createConceptGeneral(connectorEnd);
  }

  private Concept createConceptGeneral(Object object) {
    Concept concept = new Concept();

    // TODO make language configurable?
    String languageCode = "da";

    Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(object);
    Validate.isTrue(taggedValues.keySet().containsAll(getNecessaryTags()),
        EaModelUtils.toString(object)
            + " does not contain necessary tags, synchronize the stereotypes in your EA model. "
            + "\r\nNecessary tags: " + StringUtils.join(
                getNecessaryTags() + "; found tags: " + StringUtils.join(taggedValues.keySet())));

    Map<String, String> definitions = new HashMap<>();
    definitions.put(languageCode, taggedValues.get(BasicData1Constants.TAG_DEFINITION));
    concept.setDefinitions(definitions);

    Map<String, String> examples = new HashMap<>();
    examples.put(languageCode, taggedValues.get(BasicData1Constants.TAG_EXAMPLE));
    concept.setExamples(examples);

    Map<String, String> notes = new HashMap<>();
    notes.put(languageCode, taggedValues.get(BasicData1Constants.TAG_NOTE));
    concept.setNotes(notes);

    Map<String, String> preferredTerm = new HashMap<>();
    preferredTerm.put(languageCode, taggedValues.get("foretrukken term (da)"));
    concept.setPreferredTerms(preferredTerm);

    Map<String, String[]> acceptedTerms = new HashMap<>();
    acceptedTerms.put("da", TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
        "accepterede termer (da)", ','));
    concept.setAcceptedTerms(acceptedTerms);

    Map<String, String[]> deprecatedTerms = new HashMap<>();
    deprecatedTerms.put("da", TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
        "frarådede termer (da)", ','));
    concept.setDeprecatedTerms(deprecatedTerms);

    String sourceTaggedValue = taggedValues.get("kilde");
    if (StringUtils.isNotBlank(sourceTaggedValue)) {
      try {
        URI sourceUri = new URI(sourceTaggedValue);
        if ("https".equals(sourceUri.getScheme()) || "http".equals(sourceUri.getScheme())) {
          concept.setSource(sourceUri);
        } else {
          LOGGER.info(
              "Could not convert {} on {} to an absolute HTTP-URI, treating it as a textual reference",
              sourceTaggedValue, EaModelUtils.toString(object));
          concept.setSourceTextualReference(sourceTaggedValue);
        }
      } catch (URISyntaxException e) {
        LOGGER.info(
            "Could not convert {} on {} to a valid URI, ignoring message {}, treating it as a textual reference",
            sourceTaggedValue, EaModelUtils.toString(object), e.getMessage());
        concept.setSourceTextualReference(sourceTaggedValue);
      }
    }

    return concept;
  }

}
