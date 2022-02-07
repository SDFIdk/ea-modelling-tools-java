package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.ConceptModel;

/**
 * Data access object for {@link ConceptModel}s.
 */
public interface ConceptModelDao {

  ConceptModel findByPackageGuid(String packageGuid) throws ModellingToolsException;

}
