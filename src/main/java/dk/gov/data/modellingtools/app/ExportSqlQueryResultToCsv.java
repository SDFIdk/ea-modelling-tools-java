package dk.gov.data.modellingtools.app;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.sqlqueryresult.SqlQueryResultExporter;
import dk.gov.data.modellingtools.export.sqlqueryresult.impl.SqlQueryResultExporterImpl;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Exports the result of an SQL query in XML to CSV.
 */
public class ExportSqlQueryResultToCsv extends AbstractApplication {

  public static final String OPTION_SQLQUERY = "sql";
  public static final String OPTION_PARSEHTML = "parsehtml";

  @Override
  protected String getDescription() {
    return "exports the result of an SQL query in XML to CSV";
  }

  @Override
  protected void doApplicationSpecificLogic(CommandLine commandLine,
      EnterpriseArchitectWrapper eaWrapper) throws ParseException, ModellingToolsException {
    String sqlQuery = commandLine.getOptionValue(OPTION_SQLQUERY);
    String packageGuid = commandLine.getOptionValue(AbstractApplication.OPTION_PACKAGE);
    File outputFolder =
        (File) commandLine.getParsedOptionValue(AbstractApplication.OPTION_OUTPUT_FOLDER);
    boolean parseHtml = commandLine.hasOption(OPTION_PARSEHTML);
    SqlQueryResultExporter sqlQueryResultExporter = new SqlQueryResultExporterImpl(eaWrapper);
    sqlQueryResultExporter.exportToCsv(sqlQuery, parseHtml, packageGuid, outputFolder);
  }

  public static void main(String[] args) {
    new ExportSqlQueryResultToCsv().run(args);
  }

  @Override
  protected void createOptions() {
    super.createOptions();
    addOptionOutputFolder();
    addOptionalOptionPackage();
    addOptionSqlQuery();
    addOptionParseXml();
  }

  private void addOptionParseXml() {
    options.addOption(Option.builder(OPTION_PARSEHTML).longOpt("parse-html").required(false)
        .desc("specifies whether the output can be expected to contain HTML tags").build());
  }

  private void addOptionSqlQuery() {
    options.addOption(Option.builder(OPTION_SQLQUERY).longOpt("sqlquery").hasArg()
        .argName("SQL query").type(String.class).required()
        .desc("specifies the SQL query to be run (required)").build());
  }

}
