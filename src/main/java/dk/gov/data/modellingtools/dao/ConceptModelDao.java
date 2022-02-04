package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.ConceptModel;
import org.sparx.Package;

/**
 * Data access object for {@link ConceptModel}s.
 */
public interface ConceptModelDao {

  ConceptModel findByPackage(Package umlPackage) throws ModellingToolsException;

}
