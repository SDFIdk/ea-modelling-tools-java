package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sparx.Package;

public class LogicalDataModelFda implements LogicalDataModelDao {

  public LogicalDataModelFda() {
    super();
  }

  @Override
  public LogicalDataModel findByPackage(Package umlPackage) throws ModellingToolsException {
    if (umlPackage.GetElement().HasStereotype(FdaConstants.STEREOTYPE_LOGICAL_DATA_MODEL)) {
      LogicalDataModel logicalDataModel = new LogicalDataModel();
      Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(umlPackage);
      String modelName;
      if (taggedValues.containsKey(FdaConstants.TAG_TITLE_DA)) {
        // model rules 2.1
        modelName = taggedValues.get(FdaConstants.TAG_TITLE_DA);
      } else if (taggedValues.containsKey(FdaConstants.TAG_LABEL_DA)) {
        // model rules 2.0
        modelName = taggedValues.get(FdaConstants.TAG_LABEL_DA);
      } else {
        throw new ModellingToolsException(
            "Cannot find the name of the model as of the following tagged values was found on "
                + EaModelUtils.toString(umlPackage) + ":"
                + StringUtils.join(FdaConstants.TAG_TITLE_DA, FdaConstants.TAG_LABEL_DA));
      }
      logicalDataModel.setName(modelName);
      logicalDataModel.setVersion(taggedValues.get(FdaConstants.TAG_VERSION));
      return logicalDataModel;
    } else {
      throw new ModellingToolsException(EaModelUtils.toString(umlPackage)
          + " does not have stereotype " + FdaConstants.STEREOTYPE_LOGICAL_DATA_MODEL);
    }
  }

}
