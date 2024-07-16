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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;

/**
 * Abstract implementation of {@link LogicalDataModelDao}, that creates a {@link LogicalDataModel}
 * based on the tagged values of an EA package.
 */
public abstract class AbstractLogicalDataModelDao implements LogicalDataModelDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLogicalDataModelDao.class);

  protected EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public AbstractLogicalDataModelDao(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public final LogicalDataModel findByPackageGuid(String packageGuid)
      throws ModellingToolsException {
    return validateAndFindByPackageGuid(packageGuid);
  }

  /**
   * Create a logical model based on the tagged values of the package containing it.
   */
  protected abstract LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues) throws ModellingToolsException;

  protected LogicalDataModel createLogicalDataModel(Package umlPackage,
      Map<String, String> taggedValues, String tag) {
    LogicalDataModel logicalDataModel = new LogicalDataModel();
    logicalDataModel.setName(umlPackage.GetName());
    String versionFromTag = taggedValues.get(tag);
    String versionFromModel = umlPackage.GetVersion();
    String version;
    if (StringUtils.isBlank(versionFromTag)) {

      LOGGER.warn("{} does not have tag {} set, taking the version set on the package itself",
          EaModelUtils.toString(umlPackage), tag);

      version = versionFromModel;
    } else {
      version = versionFromTag;
    }
    logicalDataModel.setVersion(version);
    return logicalDataModel;
  }

  protected LogicalDataModel validateAndFindByPackageGuid(String packageGuid)
      throws ModellingToolsException {
    Package umlPackage = eaWrapper.getPackageByGuid(packageGuid);
    Collection<String> packageFqStereotypes = eaWrapper.retrievePackageFqStereotypes(umlPackage);
    if (packageFqStereotypes.contains(getFqStereotypeLogicalDataModel())) {
      Map<String, String> taggedValues = TaggedValueUtils.getTaggedValues(umlPackage.GetElement());
      return createLogicalDataModel(umlPackage, taggedValues);
    } else {
      throw new ModellingToolsException(EaModelUtils.toString(umlPackage)
          + " does not have stereotype " + getFqStereotypeLogicalDataModel());
    }
  }

  protected abstract String getFqStereotypeLogicalDataModel();

}
