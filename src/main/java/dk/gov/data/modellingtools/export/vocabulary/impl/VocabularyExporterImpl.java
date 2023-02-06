package dk.gov.data.modellingtools.export.vocabulary.impl;

import dk.gov.data.modellingtools.config.FreemarkerTemplateConfiguration;
import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.SemanticModelElementDao;
import dk.gov.data.modellingtools.dao.impl.bd1.ConceptDaoLer;
import dk.gov.data.modellingtools.dao.impl.bd1.LogicalDataModelDaoBasicData1;
import dk.gov.data.modellingtools.dao.impl.bd1.SemanticModelElementDaoBasicData1;
import dk.gov.data.modellingtools.dao.impl.bd2.LogicalDataModelDaoBasicData2;
import dk.gov.data.modellingtools.dao.impl.bd2.SemanticModelElementDaoBasicData2;
import dk.gov.data.modellingtools.dao.impl.fda.LogicalDataModelDaoFdaV20;
import dk.gov.data.modellingtools.dao.impl.fda.LogicalDataModelDaoFdaV21;
import dk.gov.data.modellingtools.dao.impl.fda.SemanticModelElementDaoFda;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.AbstractExporter;
import dk.gov.data.modellingtools.export.vocabulary.VocabularyExporter;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import dk.gov.data.modellingtools.model.ModelElement.ModelElementType;
import dk.gov.data.modellingtools.model.SemanticModelElement;
import dk.gov.data.modellingtools.utils.FolderAndFileUtils;
import freemarker.template.SimpleCollection;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.Equator;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link VocabularyExporter}.
 */
public class VocabularyExporterImpl extends AbstractExporter implements VocabularyExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyExporterImpl.class);

  private SemanticModelElementDao semanticModelElementDao;
  private LogicalDataModelDao logicalDataModelDao;

  private boolean hasHeader;
  private boolean hasMetadata;
  private URL metadataUrl;
  private LogicalDataModel logicalDataModel;
  private Locale locale;

  public VocabularyExporterImpl(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  public void exportVocabulary(String packageGuid, File folder, String format, Locale locale,
      boolean hasHeader, boolean hasMetadata, URL metadataUrl) throws ModellingToolsException {
    this.hasHeader = hasHeader;
    this.hasMetadata = hasMetadata;
    this.metadataUrl = metadataUrl;
    this.locale = locale;

    export(packageGuid, folder, format, locale);
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
    org.sparx.Package umlPackage = getEaWrapper().getPackageByGuid(getPackageGuid());
    Validate.notNull(umlPackage, "No package found with GUID %1$s", getPackageGuid());
    Collection<String> packageFqStereotypes = eaWrapper.retrievePackageFqStereotypes(umlPackage);
    if (packageFqStereotypes.contains(FdaConstants.FQ_STEREOTYPE_LOGICAL_DATA_MODEL)) {
      LOGGER.info("FDA profile is used");
      semanticModelElementDao = new SemanticModelElementDaoFda(getEaWrapper());
      if (TaggedValueUtils.getTaggedValues(umlPackage).containsKey(FdaConstants.TAG_TITLE_DA)) {
        LOGGER.info("Tag {} is present, assuming that v2.1 of the FDA profile is used and not v2.0",
            FdaConstants.TAG_TITLE_DA);
        logicalDataModelDao = new LogicalDataModelDaoFdaV21(getEaWrapper());
      } else {
        LOGGER.info(
            "Tag {} is not present, assuming that v2.0 of the FDA profile is used and not v2.1",
            FdaConstants.TAG_TITLE_DA);
        logicalDataModelDao = new LogicalDataModelDaoFdaV20(getEaWrapper());
      }
    } else if (packageFqStereotypes.contains(BasicData2Constants.FQ_STEREOTYPE_DOMAIN_MODEL)) {
      LOGGER.info("Basic data v2 profile is used");
      semanticModelElementDao = new SemanticModelElementDaoBasicData2(getEaWrapper());
      logicalDataModelDao = new LogicalDataModelDaoBasicData2(getEaWrapper());
    } else if (packageFqStereotypes.contains(BasicData1Constants.FQ_STEREOTYPE_DOMAIN_MODEL)
        && umlPackage.GetName().equals("LER")) {
      LOGGER.info("Basic data v1 profile is used, for LER datamodel"); // workaround...
      semanticModelElementDao =
          new SemanticModelElementDaoBasicData1(getEaWrapper(), new ConceptDaoLer());
      logicalDataModelDao = new LogicalDataModelDaoBasicData1(getEaWrapper());
    } else if (packageFqStereotypes.contains(BasicData1Constants.FQ_STEREOTYPE_DOMAIN_MODEL)) {
      LOGGER.info("Basic data v1 profile is used");
      semanticModelElementDao = new SemanticModelElementDaoBasicData1(getEaWrapper());
      logicalDataModelDao = new LogicalDataModelDaoBasicData1(getEaWrapper());
    } else {
      String message;
      if (packageFqStereotypes.size() == 0) {
        message = "Package " + EaModelUtils.toString(umlPackage)
            + " has no fully qualified stereotypes, check whether the stereotype is correctly set in the model";
      } else {
        message = "Cannot export vocabulary from " + EaModelUtils.toString(umlPackage)
            + " with stereotype(s) " + StringUtils.join(packageFqStereotypes, ", ");
      }
      throw new ModellingToolsException(message);
    }
  }

  @Override
  protected Map<String, Object> prepareDataForTemplate() throws ModellingToolsException {
    List<SemanticModelElement> semanticModelElements = findUniqueSemanticModelElements(locale);

    Map<String, Object> dataForTemplate = new HashMap<>();
    dataForTemplate.put("model", getLogicalDataModel());
    dataForTemplate.put("modelElements", new SimpleCollection(semanticModelElements,
        FreemarkerTemplateConfiguration.INSTANCE.getConfiguration().getObjectWrapper()));
    dataForTemplate.put("hasHeader", hasHeader);
    dataForTemplate.put("hasMetadata", hasMetadata);
    if (hasMetadata) {
      dataForTemplate.put("metadataUrl", metadataUrl.toString());
    }
    return dataForTemplate;
  }

  /**
   * Finds the {@link SemanticModelElement}s that are unique in the given language.
   *
   * @see SemanticModelElementEquator
   */
  private List<SemanticModelElement> findUniqueSemanticModelElements(Locale locale)
      throws ModellingToolsException {
    List<SemanticModelElement> allSemanticModelElements =
        semanticModelElementDao.findAllByPackageGuid(getPackageGuid());

    List<SemanticModelElement> uniqueSemanticModelElements = new ArrayList<>();
    for (SemanticModelElement semanticModelElement : allSemanticModelElements) {
      if (StringUtils.isBlank(semanticModelElement.getUmlName())) {
        LOGGER.warn("The UML name of element with id {} should not be null",
            semanticModelElement.getEaGuid());
      }
      boolean isContained = IterableUtils.contains(uniqueSemanticModelElements,
          semanticModelElement, new SemanticModelElementEquator(locale.getLanguage()));
      if (!isContained) {
        // Set uuid to null? Create other Java Bean to keep this?
        uniqueSemanticModelElements.add(semanticModelElement);
      }
    }
    uniqueSemanticModelElements
        .sort((s1, s2) -> s1.getUmlName().compareToIgnoreCase(s2.getUmlName()));
    LOGGER.info("Found {} unique semantic model elements in total",
        uniqueSemanticModelElements.size());
    return uniqueSemanticModelElements;
  }

  @Override
  protected String getTemplateFileNamePrefix() {
    return "vocabulary_";
  }

  @Override
  protected String getOutputFileName() throws ModellingToolsException {
    return FolderAndFileUtils.createFileNameWithoutSpaces(getLogicalDataModel().getName(),
        getLogicalDataModel().getVersion());
  }

  // for testing purposes
  protected void setSemanticModelElementDao(SemanticModelElementDao semanticModelElementDao) {
    this.semanticModelElementDao = semanticModelElementDao;
  }

  // for testing purposes
  protected void setLogicalDataModelDao(LogicalDataModelDao logicalDataModelDao) {
    this.logicalDataModelDao = logicalDataModelDao;
  }

  private LogicalDataModel getLogicalDataModel() throws ModellingToolsException {
    if (logicalDataModel == null) {
      logicalDataModel = logicalDataModelDao.findByPackageGuid(getPackageGuid());
    }
    return logicalDataModel;
  }

  /**
   * Two {@link SemanticModelElement}s are equal when they have the same UML name, have the same
   * {@link ModelElementType}, have the same preferred term in the given language, have the same
   * definition in the given language, have the same note in the given language, have the same legal
   * source, the same source and the same source textual reference.
   */
  private static final class SemanticModelElementEquator implements Equator<SemanticModelElement> {
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
          && areUrisEqual(sme1.getConcept().getLegalSource(), sme2.getConcept().getLegalSource())
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
