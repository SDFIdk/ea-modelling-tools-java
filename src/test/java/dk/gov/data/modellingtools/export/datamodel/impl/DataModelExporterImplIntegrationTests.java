package dk.gov.data.modellingtools.export.datamodel.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dk.gov.data.modellingtools.AbstractEaTest;
import dk.gov.data.modellingtools.app.StrictnessMode;
import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.ea.impl.EnterpriseArchitectWrapperImpl;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.datamodel.DataModelExporter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;
import org.sparx.Repository;

/**
 * Tests for export of data models created with the MDG with id {@link BasicData2Constants#MDG_ID}.
 */
public class DataModelExporterImplIntegrationTests extends AbstractEaTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DataModelExporterImplIntegrationTests.class);

  private static File folderForTest;

  private Repository repository;

  private DataModelExporter dataModelExporter;


  private Package viewPackage;

  /**
   * Creates a test directory in the temporary directory.
   */
  @BeforeAll
  public static void createTestDirectory() {
    folderForTest = new File(FileUtils.getTempDirectory(),
        DataModelExporterImplIntegrationTests.class.getSimpleName());
    LOGGER.debug("Testing in {}", folderForTest);
    boolean mkdir = folderForTest.mkdir();
    LOGGER.debug("{} created: {}", folderForTest.getAbsolutePath(), mkdir);
  }

  @BeforeEach
  public void cleanTestDirectoryAndCreateTestRepo() throws IOException {
    FileUtils.cleanDirectory(folderForTest);

    repository = createRepository(folderForTest);


    /* create a view (https://sparxsystems.com/eahelp/manageviews.html) to add packages to */
    /*
     * 0 indicates that this Package is a model (that is, it has no parent), see also
     * https://sparxsystems.com/eahelp/package_2.html
     */
    Package modelPackage = repository.GetModels().GetAt((short) 0);
    viewPackage = modelPackage.GetPackages().AddNew("View", null);
    viewPackage.Update();
    modelPackage.GetPackages().Refresh();
  }

  @AfterEach
  public void closeAndDeleteRepo() {
    closeAndDeleteRepository(repository);
  }

  @AfterAll
  public static void deleteTestDirectory() {
    FileUtils.deleteQuietly(folderForTest);
  }

  @Test
  public void testExportDataModelBasicData22_da_strict()
      throws ModellingToolsException, IOException {
    String resourcePathDataModel = "/datamodel/MinModel_GDv22.xmi";
    String languageTag = "da";
    String resourcePathExpectedOutput = "/export/datamodel/MinModel_GDv22_v1.0.0-da.csv";
    String actualOutputFileRelativePath = "MinModel_GDv22_v1.0.0.csv";
    StrictnessMode strictnessMode = StrictnessMode.STRICT;
    textExportDataModelBasicData22(resourcePathDataModel, languageTag, resourcePathExpectedOutput,
        actualOutputFileRelativePath, strictnessMode);
  }

  @Test
  public void testExportDataModelBasicData22_da_moderate()
      throws ModellingToolsException, IOException {
    String resourcePathDataModel = "/datamodel/MinModel_GDv22.xmi";
    String languageTag = "da";
    String resourcePathExpectedOutput = "/export/datamodel/MinModel_GDv22_v1.0.0-da.csv";
    String actualOutputFileRelativePath = "MinModel_GDv22_v1.0.0.csv";
    StrictnessMode strictnessMode = StrictnessMode.MODERATE;
    textExportDataModelBasicData22(resourcePathDataModel, languageTag, resourcePathExpectedOutput,
        actualOutputFileRelativePath, strictnessMode);
  }

  @Test
  public void testExportDataModelBasicData22_da_lenient()
      throws ModellingToolsException, IOException {
    String resourcePathDataModel = "/datamodel/MinModel_GDv22.xmi";
    String languageTag = "da";
    String resourcePathExpectedOutput = "/export/datamodel/MinModel_GDv22_v1.0.0-da.csv";
    String actualOutputFileRelativePath = "MinModel_GDv22_v1.0.0.csv";
    StrictnessMode strictnessMode = StrictnessMode.LENIENT;
    textExportDataModelBasicData22(resourcePathDataModel, languageTag, resourcePathExpectedOutput,
        actualOutputFileRelativePath, strictnessMode);
  }

  @Test
  public void testExportDataModelBasicData22NewLinesQuotes_da_strict()
      throws ModellingToolsException, IOException {
    String resourcePathDataModel = "/datamodel/MinModel_GDv22_TestQuotesNewLines.xmi";
    String languageTag = "da";
    String resourcePathExpectedOutput =
        "/export/datamodel/MinModel_GDv22_TestQuotesNewLines_v1.0.0-da.csv";
    String actualOutputFileRelativePath = "MinModel_GDv22_TestQuotesNewLines_v1.0.0.csv";
    StrictnessMode strictnessMode = StrictnessMode.STRICT;
    textExportDataModelBasicData22(resourcePathDataModel, languageTag, resourcePathExpectedOutput,
        actualOutputFileRelativePath, strictnessMode);
  }

  @Test
  public void testExportDataModelBasicData22NewLinesQuotes_da_lenient()
      throws ModellingToolsException, IOException {
    String resourcePathDataModel = "/datamodel/MinModel_GDv22_TestQuotesNewLines.xmi";
    String languageTag = "da";
    String resourcePathExpectedOutput =
        "/export/datamodel/MinModel_GDv22_TestQuotesNewLines_v1.0.0-da.csv";
    String actualOutputFileRelativePath = "MinModel_GDv22_TestQuotesNewLines_v1.0.0.csv";
    StrictnessMode strictnessMode = StrictnessMode.LENIENT;
    textExportDataModelBasicData22(resourcePathDataModel, languageTag, resourcePathExpectedOutput,
        actualOutputFileRelativePath, strictnessMode);
  }

  private void textExportDataModelBasicData22(String resourcePathDataModel, String languageTag,
      String resourcePathExpectedOutput, String actualOutputFileRelativePath,
      StrictnessMode strictnessMode)
      throws ModellingToolsException, IOException, FileNotFoundException {
    assumeMdgWithVersionInstalled(repository, BasicData2Constants.MDG_ID, "2.2");

    URL dataModelUrl =
        DataModelExporterImplIntegrationTests.class.getResource(resourcePathDataModel);
    String importedPackageGuid = importXmiInPackage(dataModelUrl, repository, viewPackage);
    dataModelExporter = new DataModelExporterImpl(new EnterpriseArchitectWrapperImpl(repository));
    LOGGER.info("Exporting data model with package GUID {}", importedPackageGuid);
    dataModelExporter.exportDataModel(importedPackageGuid, folderForTest, "csv",
        Locale.forLanguageTag(languageTag), strictnessMode);
    File actualOutputFile = new File(folderForTest, actualOutputFileRelativePath);
    assertTrue(actualOutputFile.exists(),
        "Expected the following file to be created " + actualOutputFile.getAbsolutePath());
    try (InputStream actualInputStream = new FileInputStream(actualOutputFile)) {
      InputStream expectedExportedInputStream = DataModelExporterImplIntegrationTests.class
          .getResourceAsStream(resourcePathExpectedOutput);
      assertTrue(IOUtils.contentEquals(expectedExportedInputStream, actualInputStream),
          "The exported file did not contain the expected contents.");
    }
  }

  // TODO add tests for model with missing tag

}
