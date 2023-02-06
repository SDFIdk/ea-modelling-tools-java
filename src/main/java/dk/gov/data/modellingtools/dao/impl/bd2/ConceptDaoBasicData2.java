package dk.gov.data.modellingtools.dao.impl.bd2;

import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.dao.impl.EaConceptDaoForLogicalDataModel;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.MapUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Element;

/**
 * Implementation of {@link ConceptDao} for the Basic Data profile v2.
 */
public class ConceptDaoBasicData2 extends EaConceptDaoForLogicalDataModel {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConceptDaoBasicData2.class);

  // TODO make configurable
  private static final char SPLIT_CHARACTER = ';';

  @Override
  public List<Concept> findAllByPackageGuid(String packageGuid) throws ModellingToolsException {
    throw new NotImplementedException("Not (yet) implemented");
  }

  @Override
  protected Concept findByObject(Object object) throws ModellingToolsException {
    Concept concept = new Concept();
    Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(object);
    Validate.isTrue(taggedValues.keySet().containsAll(BasicData2Constants.getAllModelElementTags()),
        EaModelUtils.toString(object)
            + " does not contain all required tags, synchronize the stereotypes in your EA model. "
            + "\r\nrequired tags: " + StringUtils.join(BasicData2Constants.getAllModelElementTags()
                + "; found tags: " + StringUtils.join(taggedValues.keySet())));

    try {
      concept.setIdentifier(new URI(taggedValues.get(BasicData2Constants.TAG_URI)));
    } catch (URISyntaxException e) {
      String identifier = taggedValues.get(BasicData2Constants.TAG_URI);
      LOGGER.warn("Could not convert {} on {} to a valid URI, ignoring message {}", identifier,
          EaModelUtils.toString(object), e.getMessage());
    }

    concept.setPreferredTerms(
        MapUtils.mapLanguageAndValue(taggedValues.get(BasicData2Constants.TAG_PREFERRED_LABEL_DA),
            taggedValues.get(BasicData2Constants.TAG_PREFERRED_LABEL_EN)));

    concept.setAcceptedTerms(MapUtils.mapLanguageAndValue(
        TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
            BasicData2Constants.TAG_ALTERNATIVE_LABEL_DA, SPLIT_CHARACTER),
        TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
            BasicData2Constants.TAG_ALTERNATIVE_LABEL_EN, SPLIT_CHARACTER)));

    concept.setDeprecatedTerms(MapUtils.mapLanguageAndValue(
        TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
            BasicData2Constants.TAG_DEPRECATED_LABEL_DA, SPLIT_CHARACTER),
        TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
            BasicData2Constants.TAG_DEPRECATED_LABEL_EN, SPLIT_CHARACTER)));

    concept.setDefinitions(
        MapUtils.mapLanguageAndValue(taggedValues.get(BasicData2Constants.TAG_DEFINITION_DA),
            taggedValues.get(BasicData2Constants.TAG_DEFINITION_EN)));
    concept.setExamples(
        MapUtils.mapLanguageAndValue(taggedValues.get(BasicData2Constants.TAG_EXAMPLE_DA),
            taggedValues.get(BasicData2Constants.TAG_EXAMPLE_EN)));
    concept
        .setNotes(MapUtils.mapLanguageAndValue(taggedValues.get(BasicData2Constants.TAG_COMMENT_DA),
            taggedValues.get(BasicData2Constants.TAG_COMMENT_EN)));
    setSourceOrSourceTextualReference(object, concept,
        taggedValues.get(BasicData2Constants.TAG_SOURCE));
    setLegalSource(object, concept, taggedValues.get(BasicData2Constants.TAG_LEGALSOURCE));

    if (object instanceof Element
        && ((Element) object).HasStereotype(BasicData2Constants.FQ_STEREOTYPE_CODE_LIST)) {
      setDefiningConceptModel(object, concept,
          taggedValues.get(BasicData2Constants.TAG_VOCABULARY));
    }

    return concept;
  }

}
