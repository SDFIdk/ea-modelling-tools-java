package dk.gov.data.modellingtools.dao.impl.fda;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;

/**
 * Implementation of {@link LogicalDataModelDao} for the FDA modelling rules v2.1.
 */
public class LogicalDataModelDaoFdaV21 extends AbstractLogicalDataModelFda {

  public LogicalDataModelDaoFdaV21(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected String getFqStereotypeLogicalDataModel() {
    return FdaConstants.FQ_STEREOTYPE_LOGICAL_DATA_MODEL;
  }

}
