package dk.gov.data.modellingtools.dao.impl.bd2;



import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import java.util.Map;
import org.sparx.Package;

/**
 * Abstract implementation of {@link LogicalDataModelDao} for Basic Data modelling rules v2.
 */
public abstract class AbstractLogicalDataModelDaoBasicData2 extends AbstractLogicalDataModelDao {

  public AbstractLogicalDataModelDaoBasicData2(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues) {
    return createLogicalDataModel(umlPackage, taggedValues, BasicData2Constants.TAG_VERSION);
  }

}
