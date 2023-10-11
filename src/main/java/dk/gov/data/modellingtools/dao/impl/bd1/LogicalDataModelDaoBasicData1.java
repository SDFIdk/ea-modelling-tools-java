package dk.gov.data.modellingtools.dao.impl.bd1;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;

/**
 * Implementation of {@link LogicalDataModelDao} for the Basic Data modelling rules version 1.
 */
public class LogicalDataModelDaoBasicData1 extends AbstractLogicalDataModelDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogicalDataModelDaoBasicData1.class);

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public LogicalDataModelDaoBasicData1(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  public LogicalDataModel findByPackageGuid(String packageGuid) throws ModellingToolsException {
    return validateAndFindByPackageGuid(packageGuid);
  }

  @Override
  protected LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues) {
    LogicalDataModel logicalDataModel = new LogicalDataModel();
    logicalDataModel.setName(umlPackage.GetName());
    String versionFromTag = taggedValues.get(BasicData1Constants.TAG_VERSION);
    String versionFromModel = umlPackage.GetVersion();
    String version;
    if (StringUtils.isBlank(versionFromTag)) {
      LOGGER.warn("{} does not have tag {} set, taking the version set on the package itself",
          EaModelUtils.toString(umlPackage), BasicData1Constants.TAG_VERSION);
      version = versionFromModel;
    } else {
      version = versionFromTag;
    }
    logicalDataModel.setVersion(version);
    return logicalDataModel;
  }

  @Override
  protected String getFqStereotypeLogicalDataModel() {
    return BasicData1Constants.FQ_STEREOTYPE_DOMAIN_MODEL;
  }

  @Override
  protected Collection<String> getTagsLogicalDataModel() {
    return BasicData1Constants.getAllModelTags();
  }

}
