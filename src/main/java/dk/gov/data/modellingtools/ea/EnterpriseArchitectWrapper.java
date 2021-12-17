package dk.gov.data.modellingtools.ea;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import org.sparx.Package;

public interface EnterpriseArchitectWrapper {

  void writeToScriptWindow(String message) throws ModellingToolsException;

  String sqlQuery(String query) throws ModellingToolsException;

  Package getPackageByGuid(String packageGuid) throws ModellingToolsException;

}
