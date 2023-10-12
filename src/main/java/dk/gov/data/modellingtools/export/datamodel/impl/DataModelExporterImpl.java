package dk.gov.data.modellingtools.export.datamodel.impl;

import dk.gov.data.modellingtools.app.StrictnessMode;
import dk.gov.data.modellingtools.config.FreemarkerTemplateConfiguration;
import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.ModelElementDao;
import dk.gov.data.modellingtools.dao.impl.bd1.DomainModelDaoBasicData1;
import dk.gov.data.modellingtools.dao.impl.bd1.ModelElementDaoBasicData1;
import dk.gov.data.modellingtools.dao.impl.bd2.ClassificationModelDaoBasicData2;
import dk.gov.data.modellingtools.dao.impl.bd2.DomainModelDaoBasicData2;
import dk.gov.data.modellingtools.dao.impl.bd2.ModelElementDaoBasicData2;
import dk.gov.data.modellingtools.dao.impl.fda.ClassificationModelDaoFdaV20;
import dk.gov.data.modellingtools.dao.impl.fda.ClassificationModelDaoFdaV21;
import dk.gov.data.modellingtools.dao.impl.fda.LogicalDataModelDaoFdaV20;
import dk.gov.data.modellingtools.dao.impl.fda.LogicalDataModelDaoFdaV21;
import dk.gov.data.modellingtools.dao.impl.fda.ModelElementDaoFda;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.AbstractExporter;
import dk.gov.data.modellingtools.export.datamodel.DataModelExporter;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import dk.gov.data.modellingtools.model.ModelElement;
import dk.gov.data.modellingtools.utils.FolderAndFileUtils;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exports a data model.
 */
public class DataModelExporterImpl extends AbstractExporter implements DataModelExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataModelExporterImpl.class);

  private ModelElementDao modelElementDao;

  private LogicalDataModelDao logicalDataModelDao;

  private LogicalDataModel logicalDataModel;

  private String templateFilePrefix;

  private StrictnessMode strictnessMode;

  public DataModelExporterImpl(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  public void exportDataModel(String packageGuid, File folder, String format, Locale locale,
      StrictnessMode strictnessMode) throws ModellingToolsException {
    this.strictnessMode = strictnessMode;
    LOGGER.info(
        "Export package with id {} to folder {} in format {} with locale {} and strictness mode {}",
        packageGuid, folder, format, locale, strictnessMode);
    export(packageGuid, folder, format, locale);
  }

  @Override
  public List<String> getSupportedFormats() {
    return List.of("csv");
  }

  @Override
  protected Map<String, Object> prepareDataForTemplate() throws ModellingToolsException {
    Map<String, Object> dataForTemplate = new HashMap<>();
    dataForTemplate.put("strictnessMode", strictnessMode);
    dataForTemplate.put("modelElements", new SimpleCollection(findModelElements(),
        FreemarkerTemplateConfiguration.INSTANCE.getConfiguration().getObjectWrapper()));
    TemplateHashModel enumModels = new BeansWrapperBuilder(
        FreemarkerTemplateConfiguration.INSTANCE.getConfiguration().getIncompatibleImprovements())
            .build().getEnumModels();
    try {
      dataForTemplate.put("StrictnessMode",
          enumModels.get("dk.gov.data.modellingtools.app.StrictnessMode"));
    } catch (TemplateModelException e) {
      throw new ModellingToolsException("There is a problem with the code", e);
    }
    return dataForTemplate;
  }

  private Collection<ModelElement> findModelElements() throws ModellingToolsException {
    return modelElementDao.findAllByPackageGuid(getPackageGuid());
  }

  @Override
  protected void prepareExport() throws ModellingToolsException {
    org.sparx.Package umlPackage = getEaWrapper().getPackageByGuid(getPackageGuid());
    Validate.notNull(umlPackage, "No package found with GUID %1$s", getPackageGuid());
    Collection<String> packageFqStereotypes = eaWrapper.retrievePackageFqStereotypes(umlPackage);
    if (packageFqStereotypes.contains(FdaConstants.FQ_STEREOTYPE_LOGICAL_DATA_MODEL)) {
      LOGGER.info("FDA profile is used");
      templateFilePrefix = "data_model_fda_";
      modelElementDao = new ModelElementDaoFda(getEaWrapper());
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
    } else if (packageFqStereotypes.contains(FdaConstants.FQ_STEREOTYPE_CLASSIFICATION_MODEL)) {
      LOGGER.info("FDA profile is used");
      // use same template as for domain model, for now
      templateFilePrefix = "data_model_fda_";
      modelElementDao = new ModelElementDaoFda(getEaWrapper());
      if (TaggedValueUtils.getTaggedValues(umlPackage).containsKey(FdaConstants.TAG_TITLE_DA)) {
        LOGGER.info("Tag {} is present, assuming that v2.1 of the FDA profile is used and not v2.0",
            FdaConstants.TAG_TITLE_DA);
        logicalDataModelDao = new ClassificationModelDaoFdaV21(getEaWrapper());
      } else {
        LOGGER.info(
            "Tag {} is not present, assuming that v2.0 of the FDA profile is used and not v2.1",
            FdaConstants.TAG_TITLE_DA);
        logicalDataModelDao = new ClassificationModelDaoFdaV20(getEaWrapper());
      }
    } else if (packageFqStereotypes.contains(BasicData2Constants.FQ_STEREOTYPE_DOMAIN_MODEL)) {
      LOGGER.info("Basic data v2 profile is used");
      templateFilePrefix = "data_model_basicdata2_";
      modelElementDao = new ModelElementDaoBasicData2(getEaWrapper());
      logicalDataModelDao = new DomainModelDaoBasicData2(getEaWrapper());
    } else if (packageFqStereotypes
        .contains(BasicData2Constants.FQ_STEREOTYPE_CLASSIFICATION_MODEL)) {
      // use same template as for domain model, for now
      LOGGER.info("Basic data v2 profile is used");
      templateFilePrefix = "data_model_basicdata2_";
      modelElementDao = new ModelElementDaoBasicData2(getEaWrapper());
      logicalDataModelDao = new ClassificationModelDaoBasicData2(getEaWrapper());
    } else if (packageFqStereotypes.contains(BasicData1Constants.FQ_STEREOTYPE_DOMAIN_MODEL)
        && umlPackage.GetName().equals("LER")) {
      LOGGER.info("Basic data v1 profile is used, for LER datamodel"); // workaround...
      throw new NotImplementedException("This functionality is not yet implemented"
          + "for Basic data v1 profile is used, for LER datamodel");
    } else if (packageFqStereotypes.contains(BasicData1Constants.FQ_STEREOTYPE_DOMAIN_MODEL)) {
      LOGGER.info("Basic data v1 profile is used");
      templateFilePrefix = "data_model_basicdata1_";
      modelElementDao = new ModelElementDaoBasicData1(getEaWrapper());
      logicalDataModelDao = new DomainModelDaoBasicData1(getEaWrapper());
    } else {
      String message;
      if (packageFqStereotypes.size() == 0) {
        message =
            "Package " + EaModelUtils.toString(umlPackage) + " has no fully qualified stereotypes, "
                + "check whether the stereotype is correctly set in the model";
      } else {
        message = "Cannot export " + EaModelUtils.toString(umlPackage) + " with stereotype(s) "
            + StringUtils.join(packageFqStereotypes, ", ");
      }
      throw new ModellingToolsException(message);
    }

  }

  @Override
  protected String getTemplateFileNamePrefix() {
    return templateFilePrefix;
  }

  @Override
  protected String getOutputFileName() throws ModellingToolsException {
    return FolderAndFileUtils.createFileNameWithoutSpaces(getLogicalDataModel().getName(),
        getLogicalDataModel().getVersion());
  }

  private LogicalDataModel getLogicalDataModel() throws ModellingToolsException {
    if (logicalDataModel == null) {
      logicalDataModel = logicalDataModelDao.findByPackageGuid(getPackageGuid());
    }
    return logicalDataModel;
  }

}
