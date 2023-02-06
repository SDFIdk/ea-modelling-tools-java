package dk.gov.data.modellingtools.export.vocabulary.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
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

  @BeforeEach
  public void cleanTestDirectory() throws IOException {
    FileUtils.cleanDirectory(folderForTest);
  }

  @AfterAll
  public static void deleteTestDirectory() {
    // FileUtils.deleteQuietly(folderForTest);
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
    concept1.setPreferredTerms(Map.of("da", "term klasse", "en", "term class"));
    concept1.setDefinitions(Map.of("da", "definition klasse", "en", "definition class"));
    concept1.setNotes(Map.of("da", "note klasse", "en", "note class"));
    concept1.setExamples(Map.of("da", "eksempel klasse", "en", "example class"));
    concept1.setAcceptedTerms(
        Map.of("da", new String[] {"synonym klasse"}, "en", new String[] {"synonym class"}));
    concept1.setDeprecatedTerms(Map.of("da", new String[] {"frarådet term klasse"}, "en",
        new String[] {"deprecated term class"}));
    concept1.setSourceTextualReference("kilde klasse");
    semanticModelElement1.setConcept(concept1);
    SemanticModelElement semanticModelElement2 = new SemanticModelElement();
    semanticModelElement2.setEaGuid("{B75D26A8-C0DC-413D-89B4-A2CDBF466E38}");
    semanticModelElement2.setUmlModelElementType(ModelElementType.ATTRIBUTE);
    semanticModelElement2.setUmlName("testattribut");
    Concept concept2 = new Concept();
    concept2.setPreferredTerms(Map.of("da", "term attribut", "en", "term attribute"));
    concept2.setDefinitions(Map.of("da", "definition attribut", "en", "definition attribute"));
    concept2.setNotes(Map.of("da", "note attribut", "en", "note attribute"));
    concept2.setExamples(Map.of("da", "eksempel attribut", "en", "example attribute"));
    concept2.setAcceptedTerms(
        Map.of("da", new String[] {"synonym attribut"}, "en", new String[] {"synonym attribute"}));
    concept2.setDeprecatedTerms(Map.of("da", new String[] {"frarådet term attribut"}, "en",
        new String[] {"deprecated term attribute"}));
    concept2.setSource(URI.create("https://non.existing.uri.dk"));
    semanticModelElement2.setConcept(concept2);
    SemanticModelElement semanticModelElement3 = new SemanticModelElement();
    semanticModelElement3.setEaGuid("{B75D36A8-C0DC-413D-89B4-A3CDBF466E38}");
    semanticModelElement3.setUmlModelElementType(ModelElementType.ATTRIBUTE);
    semanticModelElement3.setUmlName("testattribut2");
    Concept concept3 = new Concept();
    concept3.setPreferredTerms(Map.of("da", "term attribut 2", "en", "term attribute 2"));
    concept3.setDefinitions(Map.of("da", "definition attribut 2", "en", "definition attribute 2"));
    concept3.setNotes(Map.of("da", "note attribut 2", "en", "note attribute 2"));
    concept3.setExamples(Map.of("da", "eksempel attribut 2", "en", "example attribute 2"));
    concept3.setAcceptedTerms(Map.of("da", new String[] {"synonym attribut 2"}, "en",
        new String[] {"synonym attribute 2"}));
    concept3.setDeprecatedTerms(Map.of("da", new String[] {"frarådet term attribut 2"}, "en",
        new String[] {"deprecated term attribute 2"}));
    concept3.setLegalSource(URI.create("https://www.retsinformation.dk/eli/lta/2017/746"));
    semanticModelElement3.setConcept(concept3);
    SemanticModelElement semanticModelElement4 = new SemanticModelElement();
    semanticModelElement4.setEaGuid("{C1B46085-5F59-4FEA-8F29-504E66F9E381}");
    semanticModelElement4.setUmlModelElementType(ModelElementType.ENUMERATION);
    semanticModelElement4.setUmlName("Testkodeliste");
    Concept concept4 = new Concept();
    concept4.setPreferredTerms(Map.of("da", "term kodeliste", "en", "term code list"));
    concept4.setDefinitions(Map.of("da", "definition kodeliste", "en", "definition code list"));
    concept4.setNotes(Map.of("da", "", "en", ""));
    concept4.setExamples(Map.of("da", "", "en", ""));
    concept4.setAcceptedTerms(Map.of("da", new String[] {}, "en", new String[] {}));
    concept4.setDeprecatedTerms(Map.of("da", new String[] {}, "en", new String[] {}));
    concept4.setDefiningConceptModel(URI.create("http://id.loc.gov/vocabulary/iso639-2"));
    semanticModelElement4.setConcept(concept4);
    List<SemanticModelElement> semanticModelElements = new ArrayList<>();
    semanticModelElements.add(semanticModelElement1);
    semanticModelElements.add(semanticModelElement2);
    semanticModelElements.add(semanticModelElement3);
    semanticModelElements.add(semanticModelElement4);
    return semanticModelElements;
  }

  /**
   * Test for
   * {@link VocabularyExporterImpl#exportVocabulary(String, java.io.File, String, Locale, boolean, boolean, java.net.URL)}.
   */
  @Test
  public void testExportVocabularyDa() throws ModellingToolsException, IOException {
    textExportVocabulary("da");
  }

  /**
   * Test for
   * {@link VocabularyExporterImpl#exportVocabulary(String, java.io.File, String, Locale, boolean, boolean, java.net.URL)}.
   */
  @Test
  public void testExportVocabularyEn() throws ModellingToolsException, IOException {
    textExportVocabulary("en");
  }

  /**
   * Test for
   * {@link VocabularyExporterImpl#exportVocabulary(String, java.io.File, String, Locale, boolean, boolean, java.net.URL)}.
   */
  @Test
  public void testExportVocabularyLanguageNotSupported()
      throws ModellingToolsException, IOException {
    assertThrows(ModellingToolsException.class, () -> textExportVocabulary("nl"));
  }

  private void textExportVocabulary(String languageTag)
      throws ModellingToolsException, IOException, FileNotFoundException {
    vocabularyExporterImpl.exportVocabulary(PACKAGE_GUID, folderForTest, "csv",
        Locale.forLanguageTag(languageTag), false, false, null);
    File file = new File(folderForTest, "Test_model_v0.1.0.csv");
    LOGGER.info("Files in folder {}: {}", folderForTest.getAbsolutePath(),
        StringUtils.join(folderForTest.list(), ", "));
    assertTrue(file.exists(),
        "The file with the data vocabulary was not created or did not have the expected name.");
    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      assertTrue(
          IOUtils.contentEquals(VocabularyExporterImplTests.class.getResourceAsStream(
              "/export/testvocabulary_" + languageTag + ".csv"), fileInputStream),
          "The vocabulary did not contain the expected contents.");
    }
  }

}
