package dk.gov.data.modellingtools.dao.impl.bd1;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.dao.impl.EaConceptDaoForLogicalDataModel;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Element;

/**
 * Implementation of {@link ConceptDao} for the Basic Data profile v1.
 */
public class ConceptDaoBasicData1 extends EaConceptDaoForLogicalDataModel {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConceptDaoBasicData1.class);

  public ConceptDaoBasicData1() {
    super();
  }

  @Override
  public List<Concept> findAllByPackageGuid(String packageGuid) throws ModellingToolsException {
    throw new NotImplementedException("Not (yet) implemented");
  }

  @Override
  protected Concept findByObject(Object object) {
    Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(object);
    Validate.isTrue(taggedValues.keySet().containsAll(getNecessaryTags()),
        EaModelUtils.toString(object)
            + " does not contain the necessary tags, synchronize the stereotypes in your EA model. "
            + "\r\nNecessary tags: " + StringUtils.join(
                getNecessaryTags() + "; found tags: " + StringUtils.join(taggedValues.keySet())));

    Concept concept = new Concept();
    concept.setDefinitions(Map.of("da", taggedValues.get(BasicData1Constants.TAG_DEFINITION)));
    concept.setExamples(Map.of("da", taggedValues.get(BasicData1Constants.TAG_EXAMPLE)));
    concept.setNotes(Map.of("da", taggedValues.get(BasicData1Constants.TAG_NOTE)));
    concept.setPreferredTerms(Map.of("da", ""));
    concept.setAcceptedTerms(Map.of("da", new String[] {}));
    concept.setDeprecatedTerms(Map.of("da", new String[] {}));
    if (object instanceof Element
        && ((Element) object).HasStereotype(BasicData1Constants.FQ_STEREOTYPE_CODE_LIST)) {
      Validate.isTrue(taggedValues.containsKey(BasicData1Constants.TAG_VOCABULARY),
          EaModelUtils.toString(object) + " does not contain the tag "
              + BasicData1Constants.TAG_VOCABULARY + ", synchronize stereotype "
              + BasicData1Constants.FQ_STEREOTYPE_CODE_LIST + " in your EA model.");
      String vocabulary = taggedValues.get(BasicData1Constants.TAG_VOCABULARY);
      String vocabularyTrimmed = vocabulary.trim();
      if (!vocabulary.equals(vocabularyTrimmed)) {
        LOGGER.warn(
            "The value for tag {}, {}, contains redundant whitespace, consider updating the model",
            BasicData1Constants.TAG_VOCABULARY, vocabulary);
      }
      setDefiningConceptModel(object, concept, vocabularyTrimmed);
    }
    return concept;
  }

  private List<String> getNecessaryTags() {
    return Arrays.asList(BasicData1Constants.TAG_DEFINITION, BasicData1Constants.TAG_EXAMPLE,
        BasicData1Constants.TAG_NOTE);
  }

}
