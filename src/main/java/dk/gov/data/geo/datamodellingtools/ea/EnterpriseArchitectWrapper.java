package dk.gov.data.geo.datamodellingtools.ea;

import dk.gov.data.geo.datamodellingtools.exception.DataModellingToolsException;

public interface EnterpriseArchitectWrapper {

  String executeSqlQuery(String sqlQuery) throws DataModellingToolsException;

  void writeToScriptWindow(String message) throws DataModellingToolsException;

}
