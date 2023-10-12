package dk.gov.data.modellingtools.dao.impl.bd2;



import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;

/**
 * Abstract implementation of {@link LogicalDataModelDao} for Basic Data modelling rules v2.
 */
public abstract class AbstractLogicalDataModelDaoBasicData2 extends AbstractLogicalDataModelDao {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractLogicalDataModelDaoBasicData2.class);

  public AbstractLogicalDataModelDaoBasicData2(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues) {
    LogicalDataModel logicalDataModel = new LogicalDataModel();
    logicalDataModel.setName(umlPackage.GetName());
    String versionFromTag = taggedValues.get(BasicData2Constants.TAG_VERSION);
    String versionFromModel = umlPackage.GetVersion();
    String version;
    if (StringUtils.isBlank(versionFromTag)) {
      LOGGER.warn("{} does not have tag {} set, taking the version set on the package itself",
          EaModelUtils.toString(umlPackage), BasicData2Constants.TAG_VERSION);
      version = versionFromModel;
    } else {
      version = versionFromTag;
    }
    logicalDataModel.setVersion(version);
    return logicalDataModel;
  }

}
