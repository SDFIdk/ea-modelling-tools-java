package dk.gov.data.modellingtools.export.vocabulary.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.SemanticModelElementDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import dk.gov.data.modellingtools.model.ModelElement.ModelElementType;
import dk.gov.data.modellingtools.model.SemanticModelElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for {@link VocabularyExporterImpl}.
 */
@ExtendWith(MockitoExtension.class)
public class VocabularyExporterImplTests {


  private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyExporterImplTests.class);

  private static final String PACKAGE_GUID = "{6B60599A-CCE1-455D-9983-71B86DC5EDB3}";

  private VocabularyExporterImpl vocabularyExporterImpl;

  @Mock
  private EnterpriseArchitectWrapper eaWrapper;

  @Mock
  private SemanticModelElementDao semanticModelElementDao;

  @Mock
  private LogicalDataModelDao logicalDataModelDao;

  private static File folderForTest;

  /**
   * Creates a test directory in the temporary directory.
   */
  @BeforeAll
  public static void createTestDirectory() {
    folderForTest =
        new File(FileUtils.getTempDirectory(), VocabularyExporterImplTests.class.getSimpleName());
    LOGGER.debug("Testing in {}", folderForTest);
    boolean mkdir = folderForTest.mkdir();
    LOGGER.debug("{} created: {}", folderForTest.getAbsolutePath(), mkdir);
  }

  /**
   * Sets up {@link VocabularyExporterImpl}.
   */
  @BeforeEach
  public void createVocabularyExportImpl() throws ModellingToolsException {
    LogicalDataModel logicalDataModel = new LogicalDataModel();
    logicalDataModel.setName("Test model");
    logicalDataModel.setVersion("0.1.0");
    when(logicalDataModelDao.findByPackageGuid(PACKAGE_GUID)).thenReturn(logicalDataModel);

    List<SemanticModelElement> semanticModelElements = createSemanticModelElements();
    when(semanticModelElementDao.findAllByPackageGuid(PACKAGE_GUID))
        .thenReturn(semanticModelElements);

    vocabularyExporterImpl = new VocabularyExporterImpl(eaWrapper) {

      @Override
      protected void prepareExport() throws ModellingToolsException {
        setSemanticModelElementDao(semanticModelElementDao);
        setLogicalDataModelDao(logicalDataModelDao);
      }
    };

  }

  private List<SemanticModelElement> createSemanticModelElements() {
    SemanticModelElement semanticModelElement1 = new SemanticModelElement();
    semanticModelElement1.setEaGuid("{BBC14427-2937-4FBC-8084-4E622564032D}");
    semanticModelElement1.setUmlModelElementType(ModelElementType.CLASS);
    semanticModelElement1.setUmlName("Testklasse");
    Concept concept1 = new Concept();
    concept1.setPreferredTerms(createStringMapWithOneEntry("term klasse"));
    concept1.setDefinitions(createStringMapWithOneEntry("definition klasse"));
    concept1.setNotes(createStringMapWithOneEntry("note klasse"));
    concept1.setExamples(createStringMapWithOneEntry("eksempel klasse"));
    concept1.setAcceptedTerms(createStringArrayMapWithOneEntry("synonym klasse"));
    concept1.setDeprecatedTerms(createStringArrayMapWithOneEntry("frarådet term klasse"));
    concept1.setSourceTextualReference("kilde klasse");
    semanticModelElement1.setConcept(concept1);
    SemanticModelElement semanticModelElement2 = new SemanticModelElement();
    semanticModelElement2.setEaGuid("{B75D26A8-C0DC-413D-89B4-A2CDBF466E38}");
    semanticModelElement2.setUmlModelElementType(ModelElementType.ATTRIBUTE);
    semanticModelElement2.setUmlName("testattribut");
    Concept concept2 = new Concept();
    concept2.setPreferredTerms(createStringMapWithOneEntry("term attribut"));
    concept2.setDefinitions(createStringMapWithOneEntry("definition attribut"));
    concept2.setNotes(createStringMapWithOneEntry("note attribut"));
    concept2.setExamples(createStringMapWithOneEntry("eksempel attribut"));
    concept2.setAcceptedTerms(createStringArrayMapWithOneEntry("synonym attribut"));
    concept2.setDeprecatedTerms(createStringArrayMapWithOneEntry("frarådet term attribut"));
    concept2.setSource(URI.create("https://non.existing.uri.dk"));
    semanticModelElement2.setConcept(concept2);
    List<SemanticModelElement> semanticModelElements = new ArrayList<>();
    semanticModelElements.add(semanticModelElement1);
    semanticModelElements.add(semanticModelElement2);
    return semanticModelElements;
  }

  private Map<String, String> createStringMapWithOneEntry(String value) {
    Map<String, String> map = new HashMap<>();
    map.put("da", value);
    return map;
  }

  private Map<String, String[]> createStringArrayMapWithOneEntry(String... values) {
    Map<String, String[]> map = new HashMap<>();
    map.put("da", values);
    return map;
  }

  /**
   * Test for
   * {@link VocabularyExporterImpl#exportVocabulary(String, java.io.File, String, String, boolean, boolean, java.net.URL)}.
   */
  @Test
  public void testExportVocabulary() throws ModellingToolsException, IOException {
    vocabularyExporterImpl.exportVocabulary(PACKAGE_GUID, folderForTest, "csv", "da", false, false,
        null);
    File file = new File(folderForTest, "Test_model_v0.1.0.csv");
    LOGGER.info("Files in folder {}: {}", folderForTest.getAbsolutePath(),
        StringUtils.join(folderForTest.list(), ", "));
    assertTrue(file.exists(),
        "The file with the data vocabulary was not created or did not have the expected name.");
    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      assertTrue(IOUtils.contentEquals(
          VocabularyExporterImplTests.class.getResourceAsStream("/export/testvocabulary.csv"),
          fileInputStream), "The vocabulary did not contain the expected contents.");
    }
    FileUtils.deleteQuietly(folderForTest);
  }

}
