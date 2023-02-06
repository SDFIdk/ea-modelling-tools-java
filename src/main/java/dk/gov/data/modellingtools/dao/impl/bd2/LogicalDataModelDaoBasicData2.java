package dk.gov.data.modellingtools.dao.impl.bd2;

import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import java.util.Collection;
import java.util.Map;
import org.sparx.Package;

/**
 * Implementation of {@link LogicalDataModelDao} for the Basic Data modelling rules v2.
 */
public class LogicalDataModelDaoBasicData2 extends AbstractLogicalDataModelDao {

  public LogicalDataModelDaoBasicData2(EnterpriseArchitectWrapper eaWrapper) {
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
    logicalDataModel.setName(taggedValues.get(BasicData2Constants.TAG_TITLE_DA));
    logicalDataModel.setVersion(taggedValues.get(BasicData2Constants.TAG_VERSION));
    return logicalDataModel;
  }

  @Override
  protected String getFqStereotypeLogicalDataModel() {
    return BasicData2Constants.FQ_STEREOTYPE_DOMAIN_MODEL;
  }

  @Override
  protected Collection<String> getTagsLogicalDataModel() {
    return BasicData2Constants.getAllModelTags();
  }

}
