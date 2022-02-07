package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import java.util.List;

/**
 * Data access object for concepts.
 */
public interface ConceptDao {

  List<Concept> findAllByPackageGuid(String packageGuid) throws ModellingToolsException;

}
