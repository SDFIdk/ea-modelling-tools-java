package dk.gov.data.modellingtools.dao.impl.bd2;

import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import java.util.Collection;

/**
 * Implementation of {@link LogicalDataModelDao} for domain models of Basic Data modelling rules v2.
 */
public class DomainModelDaoBasicData2 extends AbstractLogicalDataModelDaoBasicData2 {

  public DomainModelDaoBasicData2(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected String getFqStereotypeLogicalDataModel() {
    return BasicData2Constants.FQ_STEREOTYPE_DOMAIN_MODEL;
  }

  @Override
  protected Collection<String> getTagsLogicalDataModel() {
    return BasicData2Constants.getTagsPerStereotype()
        .get(BasicData2Constants.FQ_STEREOTYPE_DOMAIN_MODEL);
  }

}
