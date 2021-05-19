package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.model.Concept;
import java.util.List;
import org.sparx.Package;

/**
 * Data access object for concepts.
 */
public interface ConceptDao {

  List<Concept> findAll(Package umlPackage);

}
