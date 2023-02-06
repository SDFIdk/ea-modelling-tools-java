package dk.gov.data.modellingtools.dao.impl.bd1;

import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of {@link ConceptDao} for the LER data model.
 */
public class ConceptDaoLer extends ConceptDaoBasicData1 {

  private static final char SPLIT_CHARACTER = ',';

  public ConceptDaoLer() {
    super();
  }

  @Override
  public List<Concept> findAllByPackageGuid(String packageGuid) throws ModellingToolsException {
    throw new NotImplementedException("Not (yet) implemented");
  }

  @Override
  protected Concept findByObject(Object object) {
    Concept concept = super.findByObject(object);
    Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(object);
    concept.setPreferredTerms(
        Map.of("da", StringUtils.defaultString(taggedValues.get("foretrukken term (da)"))));
    concept.setAcceptedTerms(Map.of("da", TaggedValueUtils.retrieveAndSplitTaggedValueValue(
        taggedValues, "accepterede termer (da)", SPLIT_CHARACTER)));
    concept.setDeprecatedTerms(Map.of("da", TaggedValueUtils
        .retrieveAndSplitTaggedValueValue(taggedValues, "frarådede termer (da)", SPLIT_CHARACTER)));
    setSourceOrSourceTextualReference(object, concept,
        StringUtils.defaultString(taggedValues.get("kilde")));
    return concept;
  }

}
