package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import java.util.Map;
import org.sparx.Package;

/**
 * Implementation of {@link LogicalDataModelDao} for the Basic Data modelling rules version 1.
 */
public class LogicalDataModelBasicData1 implements LogicalDataModelDao {

  @Override
  public LogicalDataModel findByPackage(Package umlPackage) throws ModellingToolsException {
    if (EaModelUtils.hasPackageStereotype(umlPackage,
        BasicData1Constants.STEREOTYPE_DOMAIN_MODEL)) {
      LogicalDataModel logicalDataModel = new LogicalDataModel();
      Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(umlPackage);
      logicalDataModel.setName(umlPackage.GetName());
      logicalDataModel.setVersion(taggedValues.get(BasicData1Constants.TAG_VERSION));
      return logicalDataModel;
    } else {
      throw new ModellingToolsException(EaModelUtils.toString(umlPackage)
          + " does not have stereotype " + BasicData1Constants.STEREOTYPE_DOMAIN_MODEL);
    }
  }

}
