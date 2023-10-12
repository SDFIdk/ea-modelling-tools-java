package dk.gov.data.modellingtools.dao.impl.fda;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link LogicalDataModelDao} for the FDA v2.0 profile.
 */
public class LogicalDataModelDaoFdaV20 extends AbstractLogicalDataModelFda {

  static final Logger LOGGER = LoggerFactory.getLogger(LogicalDataModelDaoFdaV20.class);

  public LogicalDataModelDaoFdaV20(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
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
