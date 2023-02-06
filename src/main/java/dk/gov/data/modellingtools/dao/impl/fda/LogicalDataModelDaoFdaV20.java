package dk.gov.data.modellingtools.dao.impl.fda;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import java.util.Collection;
import java.util.Map;
import org.sparx.Package;

/**
 * Implementation of {@link LogicalDataModelDao} for the FDA v2.0 profile.
 */
public class LogicalDataModelDaoFdaV20 extends AbstractLogicalDataModelDao {

  public LogicalDataModelDaoFdaV20(EnterpriseArchitectWrapper eaWrapper) {
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
    logicalDataModel.setName(taggedValues.get(FdaConstants.TAG_LABEL_DA));
    logicalDataModel.setVersion(taggedValues.get(FdaConstants.TAG_VERSION));
    return logicalDataModel;
  }

  @Override
  protected String getFqStereotypeLogicalDataModel() {
    return FdaConstants.FQ_STEREOTYPE_LOGICAL_DATA_MODEL;
  }

  @Override
  protected Collection<String> getTagsLogicalDataModel() {
    return FdaConstants.getAllModelTagsV20();
  }

}
