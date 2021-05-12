package dk.gov.data.geo.datamodellingtools.dao;

import dk.gov.data.geo.datamodellingtools.model.ConceptModel;
import org.sparx.Package;

public interface ConceptModelDao {

  ConceptModel findByPackage(Package umlPackage);

}
