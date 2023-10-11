package dk.gov.data.modellingtools.dao.impl.fda;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;

/**
 * Implementation of {@link LogicalDataModelDao} for the FDA modelling rules.
 */
public class LogicalDataModelDaoFdaV21 extends AbstractLogicalDataModelDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogicalDataModelDaoFdaV21.class);

  public LogicalDataModelDaoFdaV21(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  public LogicalDataModel findByPackageGuid(String packageGuid) throws ModellingToolsException {
    return validateAndFindByPackageGuid(packageGuid);
  }

  @Override
  protected LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues) throws ModellingToolsException {
    LogicalDataModel logicalDataModel = new LogicalDataModel();
    logicalDataModel.setName(umlPackage.GetName());
    String versionFromTag = taggedValues.get(FdaConstants.TAG_VERSION);
    String versionFromModel = umlPackage.GetVersion();
    String version;
    if (StringUtils.isBlank(versionFromTag)) {
      LOGGER.warn("{} does not have tag {} set, taking the version set on the package itself",
          EaModelUtils.toString(umlPackage), FdaConstants.TAG_VERSION);
      version = versionFromModel;
    } else {
      version = versionFromTag;
    }
    logicalDataModel.setVersion(version);
    return logicalDataModel;
  }

  @Override
  protected String getFqStereotypeLogicalDataModel() {
    return FdaConstants.FQ_STEREOTYPE_LOGICAL_DATA_MODEL;
  }

  @Override
  protected Collection<String> getTagsLogicalDataModel() {
    return FdaConstants.getAllModelTagsV21();
  }

}
