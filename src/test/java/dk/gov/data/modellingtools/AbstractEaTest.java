package dk.gov.data.modellingtools;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import dk.gov.data.modellingtools.constants.BasicData2Constants;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import org.apache.commons.exec.OS;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.CreateModelType;
import org.sparx.Package;
import org.sparx.Repository;

public class AbstractEaTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEaTest.class);

  protected void assumeMdgWithVersionInstalled(Repository repository, String mdgId,
      String expectedMdgVersion) {
    /*
     * TODO develop a mechanism to import and delete MDG technologies on the fly, this is currently
     * not possible with the EA API.
     */
    /*
     * The assumptions regarding the loading, enabling and version of the MDGs have to performed
     * AFTER opening the file, otherwise they will return false/an empty version.
     */
    String actualMdgVersion = repository.GetTechnologyVersion(mdgId);

    LOGGER.debug("Loaded " + repository.IsTechnologyLoaded(mdgId));
    LOGGER.debug("Enabled " + repository.IsTechnologyEnabled(mdgId));
    LOGGER.debug("Actual MDG version: " + actualMdgVersion);
    LOGGER.debug("Expected MDG version: " + expectedMdgVersion);

    assumeTrue(expectedMdgVersion.equals(actualMdgVersion),
        "The technology with id " + mdgId + " must have version " + expectedMdgVersion
            + " to run this test, not " + actualMdgVersion);
  }

  protected Repository createRepository(File folderForTest) {
    Repository repository;
    String testRepoRelativePath = "test.qea";
    assumeTrue(OS.isFamilyWindows(), "The operating system must be Windows to run this test");
    String testFile = folderForTest + "\\" + testRepoRelativePath;
    repository = new Repository();
    boolean created = repository.CreateModel(CreateModelType.cmEAPFromBase, testFile, 0);
    assertTrue(created);
    LOGGER.info("Created test repo {}", testFile);
    boolean openedFile = repository.OpenFile(testFile);
    assertTrue(openedFile);
    return repository;
  }

  protected void closeAndDeleteRepository(Repository repository) {
    if (repository != null) {
      repository.CloseFile();
      repository.Exit();
      repository.destroy();
    }
    FileUtils.deleteQuietly(new File(repository.GetConnectionString()));
    repository = null;
  }

  protected String importXmiInPackage(File dataModelFile, Repository repository, Package packageToImportIn) {
    Objects.requireNonNull(dataModelFile);
    Objects.requireNonNull(packageToImportIn);
    String xmiModelFilePath = dataModelFile.getAbsolutePath();
    /*
     * LOGGER.info("Importing XMI file {} into repo {}", xmiModelFilePath,
     * repository.GetConnectionString());
     */
    String importedPackageGuid = repository.GetProjectInterface()
        .ImportPackageXMI(packageToImportIn.GetPackageGUID(), xmiModelFilePath, 0, 0);
    return importedPackageGuid;
  }

  protected String importXmiInPackage(URL dataModelUrl, Repository repository, Package packageToImportIn) {
    Objects.requireNonNull(dataModelUrl);
    Objects.requireNonNull(packageToImportIn);
    File dataModelFile = FileUtils.toFile(dataModelUrl);
    return importXmiInPackage(dataModelFile, repository, packageToImportIn);
  }

}
