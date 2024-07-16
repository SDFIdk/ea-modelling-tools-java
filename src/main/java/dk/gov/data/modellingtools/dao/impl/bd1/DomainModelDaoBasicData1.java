package dk.gov.data.modellingtools.dao.impl.bd1;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import org.sparx.Package;

/**
 * Implementation of {@link LogicalDataModelDao} for the Basic Data modelling rules version 1.
 */
public class DomainModelDaoBasicData1 extends AbstractLogicalDataModelDao {

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public DomainModelDaoBasicData1(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues) {
    return createLogicalDataModel(umlPackage, taggedValues,
        BasicData1Constants.FQ_STEREOTYPE_DOMAIN_MODEL + "::"
            + BasicData1Constants.UNQUALIFIED_TAG_VERSION);
  }

  @Override
  protected String getFqStereotypeLogicalDataModel() {
    return BasicData1Constants.FQ_STEREOTYPE_DOMAIN_MODEL;
  }

}
