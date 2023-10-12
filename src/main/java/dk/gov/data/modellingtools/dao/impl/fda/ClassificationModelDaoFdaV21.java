package dk.gov.data.modellingtools.dao.impl.fda;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import java.util.Collection;

/**
 * Implementation of {@link LogicalDataModelDao} for the FDA modelling rules v2.1.
 */
public class ClassificationModelDaoFdaV21 extends AbstractLogicalDataModelFda {

  public ClassificationModelDaoFdaV21(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected String getFqStereotypeLogicalDataModel() {
    return FdaConstants.FQ_STEREOTYPE_CLASSIFICATION_MODEL;
  }

  @Override
  protected Collection<String> getTagsLogicalDataModel() {
    return FdaConstants.getAllModelTagsV21();
  }

}
