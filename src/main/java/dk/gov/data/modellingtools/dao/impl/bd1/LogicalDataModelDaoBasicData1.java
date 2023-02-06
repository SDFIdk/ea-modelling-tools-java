package dk.gov.data.modellingtools.dao.impl.bd1;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Map;
import org.sparx.Package;

/**
 * Implementation of {@link LogicalDataModelDao} for the Basic Data modelling rules version 1.
 */
public class LogicalDataModelDaoBasicData1 extends AbstractLogicalDataModelDao {

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
    logicalDataModel.setVersion(taggedValues.get(BasicData1Constants.TAG_VERSION));
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
