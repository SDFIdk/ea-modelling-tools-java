package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import org.sparx.Package;

/**
 * Data access object for {@link LogicalDataModel}s.
 */
public interface LogicalDataModelDao {

  LogicalDataModel findByPackage(Package umlPackage) throws ModellingToolsException;

}
