package dk.gov.data.geo.datamodellingtools.dao;

import dk.gov.data.geo.datamodellingtools.model.Concept;
import java.util.List;
import org.sparx.Package;

/**
 * Data access object for concepts.
 */
public interface ConceptDao {

  List<Concept> findAll(Package umlPackage);

}
