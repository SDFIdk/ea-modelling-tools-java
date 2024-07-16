package dk.gov.data.modellingtools.dao.impl.fda;



import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import java.util.Map;
import org.sparx.Package;

/**
 * Abstract implementation of {@link LogicalDataModelDao} for FDA rules.
 */
public abstract class AbstractLogicalDataModelFda extends AbstractLogicalDataModelDao {

  public AbstractLogicalDataModelFda(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues) throws ModellingToolsException {
    return createLogicalDataModel(umlPackage, taggedValues, FdaConstants.TAG_VERSION);
  }

}
