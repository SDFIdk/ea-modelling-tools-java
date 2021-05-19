package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.model.ConceptModel;
import org.sparx.Package;

public interface ConceptModelDao {

  ConceptModel findByPackage(Package umlPackage);

}
