package dk.gov.data.modellingtools.export.vocabulary.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dk.gov.data.modellingtools.AbstractEaTest;
import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.ea.impl.EnterpriseArchitectWrapperImpl;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.datamodel.impl.DataModelExporterImplIntegrationTests;
import dk.gov.data.modellingtools.export.vocabulary.VocabularyExporter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;
import org.sparx.Repository;

public class VocabularyExporterImplIntegrationTests extends AbstractEaTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(VocabularyExporterImplIntegrationTests.class);

  private static File folderForTest;

  private Repository repository;

  private VocabularyExporter vocabularyExporter;

  private Package viewPackage;

  /**
   * Creates a test directory in the temporary directory.
   */
  @BeforeAll
  public static void createTestDirectory() {
    folderForTest = new File(FileUtils.getTempDirectory(),
        VocabularyExporterImplIntegrationTests.class.getSimpleName());
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
  public void testExportDataVocabularyBasicData22_da()
      throws ModellingToolsException, FileNotFoundException, IOException {
    assumeMdgWithVersionInstalled(repository, BasicData2Constants.MDG_ID, "2.2");

    URL dataModelUrl =
        DataModelExporterImplIntegrationTests.class.getResource("/datamodel/MinModel_GDv22.xmi");
    String importedPackageGuid = importXmiInPackage(dataModelUrl, repository, viewPackage);

    vocabularyExporter = new VocabularyExporterImpl(new EnterpriseArchitectWrapperImpl(repository));
    vocabularyExporter.exportVocabulary(importedPackageGuid, folderForTest, "csv",
        Locale.forLanguageTag("da"), true, false, null);

    File actualOutputFile = new File(folderForTest, "MinModel_GDv22_v1.0.0.csv");
    assertTrue(actualOutputFile.exists(),
        "Expected the following file to be created " + actualOutputFile.getAbsolutePath());

    String resourcePathExpectedOutput = "/export/vocabulary/MinModel_GDv22_v1.0.0-da.csv";
    compareFileDisregardingLineOrder(resourcePathExpectedOutput, actualOutputFile);
  }

  @Test
  public void testExportDataVocabularyBasicData1()
      throws ModellingToolsException, FileNotFoundException, IOException {
    assumeMdgWithVersionInstalled(repository, BasicData1Constants.MDG_ID, "1.0");

    URL dataModelUrl =
        DataModelExporterImplIntegrationTests.class.getResource("/datamodel/MinModel_GDv1.xmi");
    String importedPackageGuid = importXmiInPackage(dataModelUrl, repository, viewPackage);

    vocabularyExporter = new VocabularyExporterImpl(new EnterpriseArchitectWrapperImpl(repository));
    vocabularyExporter.exportVocabulary(importedPackageGuid, folderForTest, "csv",
        Locale.forLanguageTag("da"), true, false, null);

    File actualOutputFile = new File(folderForTest, "MinModel_GDv1_v1.0.0.csv");
    assertTrue(actualOutputFile.exists(),
        "Expected the following file to be created " + actualOutputFile.getAbsolutePath());

    String resourcePathExpectedOutput = "/export/vocabulary/MinModel_GDv1_v1.0.0.csv";
    compareFileDisregardingLineOrder(resourcePathExpectedOutput, actualOutputFile);
  }

}

