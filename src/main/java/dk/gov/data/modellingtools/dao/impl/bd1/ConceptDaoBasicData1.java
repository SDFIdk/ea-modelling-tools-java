package dk.gov.data.modellingtools.dao.impl.bd1;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.dao.impl.EaConceptDaoForLogicalDataModel;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.ConnectorEnd;
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

    String tagPrefix = null;
    if (object instanceof Element) {
      tagPrefix = ((Element) object).GetFQStereotype() + "::";
    } else if (object instanceof Attribute) {
      tagPrefix = ((Attribute) object).GetFQStereotype() + "::";
    } else if (object instanceof ConnectorEnd) {
      LOGGER.debug(EaModelUtils.toString(object));
      if (BasicData1Constants.UQ_STEREOTYPE_PROPERTY
          .equals(((ConnectorEnd) object).GetStereotype())) {
        tagPrefix = BasicData1Constants.FQ_STEREOTYPE_PROPERTY + "::";
      }
    } else {
      tagPrefix = "";
    }

    Concept concept = new Concept();
    concept.setDefinitions(
        Map.of("da", taggedValues.get(tagPrefix + BasicData1Constants.UNQUALIFIED_TAG_DEFINITION)));
    concept.setExamples(
        Map.of("da", taggedValues.get(tagPrefix + BasicData1Constants.UNQUALIFIED_TAG_EXAMPLE)));
    concept.setNotes(
        Map.of("da", taggedValues.get(tagPrefix + BasicData1Constants.UNQUALIFIED_TAG_NOTE)));
    concept.setPreferredTerms(Map.of("da", ""));
    concept.setAcceptedTerms(Map.of("da", new String[] {}));
    concept.setDeprecatedTerms(Map.of("da", new String[] {}));
    if (object instanceof Element
        && ((Element) object).HasStereotype(BasicData1Constants.FQ_STEREOTYPE_CODE_LIST)) {
      Validate.isTrue(
          taggedValues.containsKey(tagPrefix + BasicData1Constants.UNQUALIFIED_TAG_VOCABULARY),
          EaModelUtils.toString(object) + " does not contain the tag " + tagPrefix
              + BasicData1Constants.UNQUALIFIED_TAG_VOCABULARY + ", synchronize stereotype "
              + BasicData1Constants.FQ_STEREOTYPE_CODE_LIST + " in your EA model.");
      String vocabulary =
          taggedValues.get(tagPrefix + BasicData1Constants.UNQUALIFIED_TAG_VOCABULARY);
      setDefiningConceptModel(object, concept, vocabulary);
    }
    return concept;
  }

}
