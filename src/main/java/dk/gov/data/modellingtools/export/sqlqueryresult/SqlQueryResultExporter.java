package dk.gov.data.modellingtools.export.sqlqueryresult;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;

/**
 * Exports the result of an SQL query.
 */
public interface SqlQueryResultExporter {

  /**
   * Exports the result of the given SQL query, run on the given package (optional), to the given
   * output folder.
   */
  void exportToCsv(String sqlQuery, boolean parseHtml, String packageGuid, File outputFolder)
      throws ModellingToolsException;

}
