package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;

/**
 * Data access object for {@link LogicalDataModel}s.
 */
public interface LogicalDataModelDao {

  LogicalDataModel findByPackageGuid(String packageGuid) throws ModellingToolsException;

}
