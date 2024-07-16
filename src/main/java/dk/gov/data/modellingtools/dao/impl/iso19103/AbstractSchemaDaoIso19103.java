package dk.gov.data.modellingtools.dao.impl.iso19103;

import dk.gov.data.modellingtools.constants.Iso19103Constants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.dao.impl.AbstractLogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import java.util.Map;
import org.sparx.Package;

/**
 * Abstract implementation of {@link LogicalDataModelDao} for the ISO 19103 profile.
 */
public class AbstractSchemaDaoIso19103 extends AbstractLogicalDataModelDao {

  public AbstractSchemaDaoIso19103(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues) throws ModellingToolsException {
    return createLogicalDataModel(umlPackage, taggedValues, Iso19103Constants.TAG_VERSION);
  }

  @Override
  protected String getFqStereotypeLogicalDataModel() {
    // TODO rename LogicalDataModel? An abstract is not a logical data model...
    return Iso19103Constants.FQ_STEREOTYPE_ABSTRACT_SCHEMA;
  }


}
