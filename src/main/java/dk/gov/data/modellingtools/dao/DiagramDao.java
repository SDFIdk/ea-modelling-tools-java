package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.model.Diagram;
import java.util.List;
import org.sparx.Package;

/**
 * Data access object for diagrams.
 */
public interface DiagramDao {

  List<Diagram> findAll(Package umlPackage);

}
