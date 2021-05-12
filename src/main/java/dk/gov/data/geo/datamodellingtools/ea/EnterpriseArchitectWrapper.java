package dk.gov.data.geo.datamodellingtools.ea;

import dk.gov.data.geo.datamodellingtools.exception.DataModellingToolsException;
import org.sparx.Package;

public interface EnterpriseArchitectWrapper {

  void writeToScriptWindow(String message) throws DataModellingToolsException;

  String sqlQuery(String query) throws DataModellingToolsException;

  Package getPackageByGuid(String packageGuid) throws DataModellingToolsException;

}
