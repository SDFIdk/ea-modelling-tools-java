package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Diagram;
import java.util.List;

/**
 * Data access object for diagrams.
 */
public interface DiagramDao {

  List<Diagram> findAllByPackageGuid(String packageGuid) throws ModellingToolsException;

}
