package dk.gov.data.modellingtools.export.vocabulary.impl;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.SemanticModelElementDao;
import dk.gov.data.modellingtools.dao.impl.LogicalDataModelBasicData1;
import dk.gov.data.modellingtools.dao.impl.LogicalDataModelFda;
import dk.gov.data.modellingtools.dao.impl.SemanticModelElementDaoBasicData1;
import dk.gov.data.modellingtools.dao.impl.SemanticModelElementDaoFda;
import dk.gov.data.modellingtools.dao.impl.SemanticModelElementDaoLer;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.AbstractExporter;
import dk.gov.data.modellingtools.export.vocabulary.VocabularyExporter;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import dk.gov.data.modellingtools.model.ModelElement.ModelElementType;
import dk.gov.data.modellingtools.model.SemanticModelElement;
import dk.gov.data.modellingtools.utils.FolderAndFileUtils;
import freemarker.template.Configuration;
import freemarker.template.SimpleCollection;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.Equator;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VocabularyExporterImpl extends AbstractExporter implements VocabularyExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyExporterImpl.class);

  private SemanticModelElementDao semanticModelElementDao;
  private LogicalDataModelDao logicalDataModelDao;

  private boolean hasHeader;
  private boolean hasMetadata;
  private URL metadataUrl;
  private LogicalDataModel logicalDataModel;
  private String language;

  public VocabularyExporterImpl(EnterpriseArchitectWrapper eaWrapper,
      Configuration templateConfiguration) {
    super(eaWrapper, templateConfiguration);
  }

  @Override
  public void exportVocabulary(String packageGuid, File folder, String format, String language,
      boolean hasHeader, boolean hasMetadata, URL metadataUrl) throws ModellingToolsException {
    this.hasHeader = hasHeader;
    this.hasMetadata = hasMetadata;
    this.metadataUrl = metadataUrl;
    this.language = language;

    export(packageGuid, folder, format);
  }

  @Override
  public List<String> getSupportedFormats() {
    return List.of("csv");
  }

  @Override
  protected void prepareExport() throws ModellingToolsException {
    /*
     * Models can have both the FDA and the Basic Data stereotypes, but in that case, the semantic
     * information will be in the FDA tags, not in the Basic Data tags. Therefore, check the FDA
     * stereotype first.
     */
    if (EaModelUtils.hasPackageStereotype(getPackage(),
        FdaConstants.STEREOTYPE_LOGICAL_DATA_MODEL)) {
      LOGGER.info("FDA profile is used");
      semanticModelElementDao = new SemanticModelElementDaoFda(getEaWrapper());
      logicalDataModelDao = new LogicalDataModelFda();
    } else if (EaModelUtils.hasPackageStereotype(getPackage(),
        BasicData1Constants.STEREOTYPE_DOMAIN_MODEL) && getPackage().GetName().equals("LER")) {
      LOGGER.info("Basic data v1 profile is used, for LER datamodel"); // workaround...
      semanticModelElementDao = new SemanticModelElementDaoLer(getEaWrapper());
      logicalDataModelDao = new LogicalDataModelBasicData1();
    } else if (EaModelUtils.hasPackageStereotype(getPackage(),
        BasicData1Constants.STEREOTYPE_DOMAIN_MODEL)) {
      LOGGER.info("Basic data v1 profile is used");
      semanticModelElementDao = new SemanticModelElementDaoBasicData1(getEaWrapper());
      logicalDataModelDao = new LogicalDataModelBasicData1();
    } else {
      throw new ModellingToolsException(
          "Cannot export vocabulary from " + EaModelUtils.toString(getPackage())
              + " with stereotype(s) " + getPackage().GetElement().GetStereotypeEx());
    }
    logicalDataModel = logicalDataModelDao.findByPackage(getPackage());
  }

  @Override
  protected Map<String, Object> prepareDataForTemplate() throws ModellingToolsException {
    List<SemanticModelElement> semanticModelElements = findUniqueSemanticModelElements(language);

    Map<String, Object> dataForTemplate = new HashMap<>();
    dataForTemplate.put("model", logicalDataModel);
    dataForTemplate.put("modelElements",
        new SimpleCollection(semanticModelElements, templateConfiguration.getObjectWrapper()));
    dataForTemplate.put("language", language);
    dataForTemplate.put("hasHeader", hasHeader);
    dataForTemplate.put("hasMetadata", hasMetadata);
    if (hasMetadata) {
      dataForTemplate.put("metadataUrl", metadataUrl.toString());
    }
    return dataForTemplate;
  }

  /**
   * @see SemanticModelElementEquator
   */
  private List<SemanticModelElement> findUniqueSemanticModelElements(String language)
      throws ModellingToolsException {
    LOGGER.info("Finding semantic model elements in " + EaModelUtils.toString(getPackage()));
    List<SemanticModelElement> allSemanticModelElements =
        semanticModelElementDao.findAll(getPackage());
    LOGGER.info("Found " + allSemanticModelElements.size() + " semantic model elements in total");

    List<SemanticModelElement> uniqueSemanticModelElements = new ArrayList<>();
    for (SemanticModelElement semanticModelElement : allSemanticModelElements) {
      if (StringUtils.isBlank(semanticModelElement.getUmlName())) {
        LOGGER.warn("The UML name of element with id " + semanticModelElement.getEaGuid()
            + " should not be null");
      }
      boolean isContained = IterableUtils.contains(uniqueSemanticModelElements,
          semanticModelElement, new SemanticModelElementEquator(language));
      if (!isContained) {
        // Set uuid to null? Create other Java Bean to keep this?
        uniqueSemanticModelElements.add(semanticModelElement);
      }
    }
    uniqueSemanticModelElements
        .sort((s1, s2) -> s1.getUmlName().compareToIgnoreCase(s2.getUmlName()));
    LOGGER.info(
        "Found " + uniqueSemanticModelElements.size() + " unique semantic model elements in total");
    return uniqueSemanticModelElements;
  }

  @Override
  protected String getTemplateFileNamePrefix() {
    return "vocabulary_";
  }

  @Override
  protected String getOutputFileName() {
    return FolderAndFileUtils.createFileNameWithoutSpaces(logicalDataModel.getName(),
        logicalDataModel.getVersion());
  }

  /**
   * Two {@link SemanticModelElement}s are equal when they have the same UML name, have the same
   * {@link ModelElementType}, have the same preferred term in the given language, have the same
   * definition in the given language, have the same note in the given language, have the same
   * source and the same source textual reference.
   */
  private final class SemanticModelElementEquator implements Equator<SemanticModelElement> {
    private final String language;

    private SemanticModelElementEquator(String language) {
      this.language = language;
    }

    @Override
    public boolean equate(SemanticModelElement sme1, SemanticModelElement sme2) {
      return sme1.getUmlModelElementType().equals(sme2.getUmlModelElementType())
          && sme1.getUmlName().equals(sme2.getUmlName())
          && StringUtils.equals(sme1.getConcept().getPreferredTerms().get(language),
              sme2.getConcept().getPreferredTerms().get(language))
          && StringUtils.equals(sme1.getConcept().getDefinitions().get(language),
              sme2.getConcept().getDefinitions().get(language))
          && StringUtils.equals(sme1.getConcept().getNotes().get(language),
              sme2.getConcept().getNotes().get(language))
          && areUrisEqual(sme1.getConcept().getSource(), sme2.getConcept().getSource())
          && StringUtils.equals(sme1.getConcept().getSourceTextualReference(),
              sme2.getConcept().getSourceTextualReference());
    }

    private boolean areUrisEqual(URI uri1, URI uri2) {
      if (uri1 == uri2) {
        return true;
      }
      if (uri1 == null || uri2 == null) {
        return false;
      }
      return uri1.equals(uri2);
    }

    @Override
    public int hash(SemanticModelElement sme) {
      return Objects.hash(sme.getUmlModelElementType(), sme.getUmlName(),
          sme.getConcept().getPreferredTerms().get(language),
          sme.getConcept().getDefinitions().get(language),
          sme.getConcept().getNotes().get(language), sme.getConcept().getSource(),
          sme.getConcept().getSourceTextualReference());
    }
  }

}
