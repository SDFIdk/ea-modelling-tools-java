package dk.gov.data.modellingtools.dao.impl.fda;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.impl.EaConceptDaoForLogicalDataModel;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.MapUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation based on the FDA rules for data modelling.
 */
public class ConceptDaoFdaForLogicalDataModel extends EaConceptDaoForLogicalDataModel {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ConceptDaoFdaForLogicalDataModel.class);

  // TODO make configurable
  private static final char SPLIT_CHARACTER = ';';

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public ConceptDaoFdaForLogicalDataModel() {
    super();
  }

  @Override
  public List<Concept> findAllByPackageGuid(String packageGuid) throws ModellingToolsException {
    throw new NotImplementedException("Not (yet) implemented");
  }

  @Override
  protected Concept findByObject(Object object) throws ModellingToolsException {
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

    concept.setPreferredTerms(
        MapUtils.mapLanguageAndValue(taggedValues.get(FdaConstants.TAG_PREFERRED_LABEL_DA),
            taggedValues.get(FdaConstants.TAG_PREFERRED_LABEL_EN)));

    concept.setAcceptedTerms(MapUtils.mapLanguageAndValue(
        TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
            FdaConstants.TAG_ALTERNATIVE_LABEL_DA, SPLIT_CHARACTER),
        TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
            FdaConstants.TAG_ALTERNATIVE_LABEL_EN, SPLIT_CHARACTER)));

    concept.setDeprecatedTerms(MapUtils.mapLanguageAndValue(
        TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
            FdaConstants.TAG_DEPRECATED_LABEL_DA, SPLIT_CHARACTER),
        TaggedValueUtils.retrieveAndSplitTaggedValueValue(taggedValues,
            FdaConstants.TAG_DEPRECATED_LABEL_EN, SPLIT_CHARACTER)));

    concept.setDefinitions(
        MapUtils.mapLanguageAndValue(taggedValues.get(FdaConstants.TAG_DEFINITION_DA),
            taggedValues.get(FdaConstants.TAG_DEFINITION_EN)));
    concept.setExamples(MapUtils.mapLanguageAndValue(taggedValues.get(FdaConstants.TAG_EXAMPLE_DA),
        taggedValues.get(FdaConstants.TAG_EXAMPLE_EN)));
    concept.setNotes(MapUtils.mapLanguageAndValue(taggedValues.get(FdaConstants.TAG_COMMENT_DA),
        taggedValues.get(FdaConstants.TAG_COMMENT_EN)));

    setSourceOrSourceTextualReference(object, concept, taggedValues.get(FdaConstants.TAG_SOURCE));
    setLegalSource(object, concept, taggedValues.get(FdaConstants.TAG_LEGALSOURCE));

    try {
      concept.setDefiningConceptModel(new URI(taggedValues.get(FdaConstants.TAG_IS_DEFINED_BY)));
    } catch (URISyntaxException e) {
      String definingConceptModelUri = taggedValues.get(FdaConstants.TAG_IS_DEFINED_BY);
      LOGGER.info("Could not convert {} to a valid URI, ignoring message {}",
          definingConceptModelUri, e.getMessage());
    }
    return concept;
  }

}
