package dk.gov.data.modellingtools.update.datamodel.impl;

import dk.gov.data.modellingtools.app.TagUpdateMode;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.ModelElement.ModelElementType;
import dk.gov.data.modellingtools.update.datamodel.DataModelTagsUpdater;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Connector;

/**
 * Implementation of {@link DataModelTagsUpdater}. Headers "GUID" and "TYPE" must be present in the
 * input file. Headers "GUID", "UML-NAVN", "NAMESPACE", "TYPE", "STEREOTYPE" are standard headers,
 * the other headers are assumed to be the name of the tagged value.
 */
public class DataModelTagsUpdaterImpl implements DataModelTagsUpdater {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataModelTagsUpdaterImpl.class);

  private EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public DataModelTagsUpdaterImpl(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public void updateDataModel(String packageGuid, File file, TagUpdateMode tagUpdateMode)
      throws ModellingToolsException {
    Objects.requireNonNull(packageGuid);
    Objects.requireNonNull(file);
    org.sparx.Package umlPackage = eaWrapper.getPackageByGuid(packageGuid);
    Validate.notNull(umlPackage, "No package found with GUID %1$s", packageGuid);

    Validate.isTrue(FilenameUtils.isExtension(file.getAbsolutePath(), "csv"),
        "The file must be a CSV file");

    LOGGER.info("Import data from file {} into package with GUID {}", file, packageGuid);
    try {
      CSVParser csvParser =
          CSVParser.parse(new FileReader(file, StandardCharsets.UTF_8), getCsvFormat());

      processCsv(umlPackage, csvParser, tagUpdateMode);
    } catch (IOException e) {
      throw new ModellingToolsException("Could not parse file " + file.getAbsolutePath(), e);
    }

  }

  private CSVFormat getCsvFormat() {
    return CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).build();
  }

  /**
   * Reads the CSV data from a string instead of from a file. This is useful in unit testing any
   * newlines issues, as we cannot be sure that the newlines in the files are not changed by the
   * version control system or the system containing a working copy. See also
   * https://git-scm.com/book/en/v2/Customizing-Git-Git-Configuration#_formatting_and_whitespace.
   */
  void updateDataModel(String packageGuid, String csvString, TagUpdateMode tagUpdateMode)
      throws ModellingToolsException {
    Objects.requireNonNull(packageGuid);
    org.sparx.Package umlPackage = eaWrapper.getPackageByGuid(packageGuid);
    Validate.notNull(umlPackage, "No package found with GUID %1$s", packageGuid);

    try {
      CSVParser csvParser = CSVParser.parse(csvString, getCsvFormat());

      processCsv(umlPackage, csvParser, tagUpdateMode);
    } catch (IOException e) {
      throw new ModellingToolsException("Could not parse string " + csvString, e);
    }

  }

  private void processCsv(org.sparx.Package umlPackage, CSVParser csvParser,
      TagUpdateMode tagUpdateMode) throws ModellingToolsException {
    final List<String> allHeaders = csvParser.getHeaderNames();
    final List<String> minimumExpectedHeaders = List.of("GUID", "TYPE");
    final List<String> standardHeaders =
        List.of("GUID", "UML-NAVN", "NAMESPACE", "TYPE", "STEREOTYPE");
    Validate.isTrue(allHeaders.containsAll(minimumExpectedHeaders),
        "Expected the following headers to be present: %1$", minimumExpectedHeaders);
    final List<String> nonStandardHeaders = ListUtils.removeAll(allHeaders, standardHeaders);
    LOGGER.info("Found the following tags in the file: {}", nonStandardHeaders.toString());

    Collection<Connector> associations =
        EaModelUtils.getAllAssociationsOfPackageAndSubpackages(umlPackage);

    for (CSVRecord record : csvParser.getRecords()) {
      String modelElementGuid = record.get("GUID");
      if (StringUtils.isEmpty(modelElementGuid)) {
        /*
         * The "Copy Selected to Clipboard" functionality
         * (https://sparxsystems.com/eahelp/model_search_context_menu.html) generates
         * semicolon-separated values, and a empty second line. When that contents is copied into
         * LibreOffice Calc and then saved as a CSV file, the second line in the file will only
         * consist of comma's. Therefore, ignore any records with an empty GUID. This way, this
         * class is lenient towards users forgetting to remove that empty line.
         */
        LOGGER.info("Ignoring line with empty GUID");
      } else {
        String type = record.get("TYPE");
        LOGGER.debug("Processing model element with GUID {} and type ", modelElementGuid, type);
        Object object = null;
        ModelElementType modelElementType = ModelElementType.valueOf(type);
        switch (modelElementType) {
          case CLASS:
          case DATA_TYPE:
          case ENUMERATION:
          case INTERFACE:
            object = eaWrapper.getElementByGuid(modelElementGuid);
            break;
          case ATTRIBUTE:
          case ENUMERATION_LITERAL:
            object = eaWrapper.getAttributeByGuid(modelElementGuid);
            break;
          case ASSOCIATION_END:
            object = EaModelUtils.findConnectorEnd(associations, modelElementGuid);
            break;
          case ASSOCIATION:
            object = eaWrapper.getConnectorByGuid(modelElementGuid);
            break;
          default:
            throw new ModellingToolsException(
                "Cannot deal with object of type " + modelElementType);
        }
        Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(object);
        List<String> headersToUse;
        boolean createIfNotPresentAndDeleteIfEmptyString;
        switch (tagUpdateMode) {
          case UPDATE_ONLY:
            headersToUse = ListUtils.retainAll(nonStandardHeaders, taggedValues.keySet());
            createIfNotPresentAndDeleteIfEmptyString = false;
            break;
          case UPDATE_ADD_DELETE:
            headersToUse = nonStandardHeaders;
            createIfNotPresentAndDeleteIfEmptyString = true;
            break;
          default:
            throw new ModellingToolsException("Cannot deal with tag update mode " + tagUpdateMode);
        }
        /*
         * remove all the tagged values from the map that are not present in the header of the file
         * to import
         */
        Set<String> presentTaggedValues = Set.copyOf(taggedValues.keySet());
        for (String taggedValueName : presentTaggedValues) {
          if (!headersToUse.contains(taggedValueName)) {
            taggedValues.remove(taggedValueName);
          }
        }

        // now update the map with tagged values from the file...
        for (String headerName : headersToUse) {
          String valueFromRecord = record.get(headerName);
          /*
           * Any newline has to be as a carriage return (\r) plus a line feed (\n), otherwise EA
           * will not process it as expected. LibreOffice Calc, often used to edit CSV files, saves
           * a newline in a cell as only a line feed.
           */
          String valueToSave = valueFromRecord.replaceAll("(?<!\r)\n", "\r\n");
          taggedValues.put(headerName, valueToSave);
        }

        /*
         * ... and save them into the underlying database, but taking into account the value of
         * createIfNotPresentAndDeleteIfEmptyString
         */
        TaggedValueUtils.setTaggedValues(object, taggedValues,
            createIfNotPresentAndDeleteIfEmptyString);
        LOGGER.info("Updated tagged values of {}", EaModelUtils.toString(object));
      }
    }
  }



}
