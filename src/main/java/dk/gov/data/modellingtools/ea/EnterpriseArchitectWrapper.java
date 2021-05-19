package dk.gov.data.modellingtools.ea;

import dk.gov.data.modellingtools.exception.DataModellingToolsException;
import org.sparx.Package;

public interface EnterpriseArchitectWrapper {

  void writeToScriptWindow(String message) throws DataModellingToolsException;

  String sqlQuery(String query) throws DataModellingToolsException;

  Package getPackageByGuid(String packageGuid) throws DataModellingToolsException;

}
