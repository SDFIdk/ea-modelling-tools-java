package dk.gov.data.modellingtools.export.sqlqueryresult.impl;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.sqlqueryresult.SqlQueryResultExporter;
import dk.gov.data.modellingtools.utils.XmlAndXsltUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmAtomicValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;

/**
 * Exports the result of an SQL query, formatted as XML, to CSV and write that to a file. The
 * transformation from XML to CSV is done using XSLT.
 */
public class SqlQueryResultExporterImpl implements SqlQueryResultExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqlQueryResultExporterImpl.class);

  private EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public SqlQueryResultExporterImpl(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public void exportToCsv(String sqlQuery, boolean parseHtml, String packageGuid, File outputFolder)
      throws ModellingToolsException {
    Objects.requireNonNull(sqlQuery);
    Objects.requireNonNull(outputFolder);

    LOGGER.info("Export SQL query result in XML to CSV");
    LOGGER.debug("SQL query: {}", sqlQuery);
    String expandedSqlQuery = expandMacros(sqlQuery, packageGuid);
    LOGGER.debug("Expanded SQL query: {}", expandedSqlQuery);
    String sqlQueryResult = eaWrapper.sqlQuery(expandedSqlQuery);
    LOGGER.trace("SQL query result: {}", sqlQueryResult);
    File outputFile = new File(outputFolder, "sqlqueryresult.csv");
    Map<QName, XdmAtomicValue> parameters = new HashMap<>();
    parameters.put(new QName("parseHtml"), new XdmAtomicValue(parseHtml));
    XmlAndXsltUtils.transformXml(sqlQueryResult, "/export/sqlqueryxml2csv.xsl", parameters,
        outputFile);
    LOGGER.info("Finished exporting SQL query XML to file " + outputFile.getAbsolutePath());
  }

  /**
   * Expands the macros in the given SQL query. Currently, only #Branch# is supported.
   *
   * @see <a href="https://sparxsystems.com/eahelp/creating_filters.html">Create Search
   *      Definitions</a> in the EA User Guide
   */
  private String expandMacros(String sqlQuery, String packageGuid) throws ModellingToolsException {
    final String sqlQueryMacrosExpanded;
    if (packageGuid == null) {
      sqlQueryMacrosExpanded = sqlQuery;
    } else {
      Package eaPackage = eaWrapper.getPackageByGuid(packageGuid);
      Collection<Integer> packageIds =
          getPackageIds(eaPackage, EaModelUtils.getSubpackages(eaPackage));
      sqlQueryMacrosExpanded =
          StringUtils.replace(sqlQuery, "#Branch#", StringUtils.join(packageIds, ','));
    }
    return sqlQueryMacrosExpanded;
  }

  private Collection<Integer> getPackageIds(Package eaPackage, Collection<Package> subpackages) {
    Collection<Integer> packageIds = new ArrayList<>();
    packageIds.add(eaPackage.GetPackageID());
    for (Package subpackage : subpackages) {
      packageIds.add(subpackage.GetPackageID());
    }
    return packageIds;
  }

}
