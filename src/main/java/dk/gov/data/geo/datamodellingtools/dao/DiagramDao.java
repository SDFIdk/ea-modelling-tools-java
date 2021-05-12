package dk.gov.data.geo.datamodellingtools.dao;

import dk.gov.data.geo.datamodellingtools.model.Diagram;
import java.util.List;
import org.sparx.Package;

/**
 * Data access object for diagrams.
 */
public interface DiagramDao {

  List<Diagram> findAll(Package umlPackage);

}
