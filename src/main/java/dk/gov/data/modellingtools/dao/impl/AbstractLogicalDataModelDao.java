package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.LogicalDataModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.LogicalDataModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.sparx.Package;

/**
 * Abstract implementation of {@link LogicalDataModelDao}, that creates a {@link LogicalDataModel}
 * based on the tagged values of an EA package.
 */
public abstract class AbstractLogicalDataModelDao implements LogicalDataModelDao {

  protected EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public AbstractLogicalDataModelDao(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  /**
   * Create a logical model based on the tagged values of the package containing it.
   */
  protected abstract LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues) throws ModellingToolsException;

  protected LogicalDataModel validateAndFindByPackageGuid(String packageGuid)
      throws ModellingToolsException {
    Package umlPackage = eaWrapper.getPackageByGuid(packageGuid);
    Collection<String> packageFqStereotypes = eaWrapper.retrievePackageFqStereotypes(umlPackage);
    if (packageFqStereotypes.contains(getFqStereotypeLogicalDataModel())) {
      Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(umlPackage.GetElement());
      Validate.isTrue(taggedValues.keySet().containsAll(getTagsLogicalDataModel()), EaModelUtils
          .toString(umlPackage)
          + " does not contain all expected tags, synchronize the stereotypes in your EA model. "
          + "\r\nexpected tags: " + StringUtils.join(getTagsLogicalDataModel() + ";\r\nfound tags: "
              + StringUtils.join(taggedValues.keySet())));
      return createLogicalDataModel(umlPackage, taggedValues);
    } else {
      throw new ModellingToolsException(EaModelUtils.toString(umlPackage)
          + " does not have stereotype " + getFqStereotypeLogicalDataModel());
    }
  }

  protected abstract String getFqStereotypeLogicalDataModel();

  protected abstract Collection<String> getTagsLogicalDataModel();

}
