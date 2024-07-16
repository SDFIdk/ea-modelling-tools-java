package dk.gov.data.modellingtools.dao.impl.iso19103;

import dk.gov.data.modellingtools.constants.Iso19103Constants;
import dk.gov.data.modellingtools.dao.impl.AbstractModelElementDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import java.util.Collection;
import org.apache.commons.collections4.MultiValuedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.Element;

/**
 * Implementation of {@link AbstractModelElementDao} for the ISO 19103 profile.
 */
public class ModelElementDaoIso19103 extends AbstractModelElementDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(ModelElementDaoIso19103.class);

  public ModelElementDaoIso19103(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected boolean isRelevantModelElement(Element element,
      MultiValuedMap<String, String> elementFqStereotypes) {
    Collection<String> stereotypes = elementFqStereotypes.get(element.GetElementGUID());
    if (stereotypes.size() > 1) {
      LOGGER.warn("{} has more than one stereotype: {}", EaModelUtils.toString(element),
          stereotypes);
    }
    return EaModelUtils.getDataModellingClassifiers().contains(element.GetType())
        && (element.HasStereotype(Iso19103Constants.FQ_STEREOTYPE_CLASS)
            || element.HasStereotype(Iso19103Constants.FQ_STEREOTYPE_DATA_TYPE)
            || element.HasStereotype(Iso19103Constants.FQ_STEREOTYPE_ENUMERATION)
            || element.HasStereotype(Iso19103Constants.FQ_STEREOTYPE_INTERFACE))
        && stereotypes.size() == 1;
  }

  @Override
  protected boolean isRelevantModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    Collection<String> stereotypes = attributeFqStereotypes.get(attribute.GetAttributeGUID());
    if (stereotypes.size() > 1) {
      LOGGER.warn("{} has more than one stereotype: {}", EaModelUtils.toString(attribute),
          stereotypes);
    }
    return (attributeFqStereotypes.containsMapping(attribute.GetAttributeGUID(),
        Iso19103Constants.FQ_STEREOTYPE_PROPERTY)
        || attributeFqStereotypes.containsMapping(attribute.GetAttributeGUID(),
            Iso19103Constants.FQ_STEREOTYPE_ENUMERATION_LITERAL))
        && stereotypes.size() == 1;
  }

  @Override
  protected boolean isOppositeConnectorEndRelevantModelElement(EaConnectorEnd eaConnectorEnd,
      MultiValuedMap<String, String> connectorEndFqStereotypes) {
    Collection<String> stereotypes =
        connectorEndFqStereotypes.get(eaConnectorEnd.getOppositeConnectorEndUniqueId());
    if (stereotypes.size() > 1) {
      LOGGER.warn("{} has more than one stereotype: {}",
          EaModelUtils.toString(eaConnectorEnd.getOppositeConnectorEnd()), stereotypes);
    }
    return connectorEndFqStereotypes.containsMapping(
        eaConnectorEnd.getOppositeConnectorEndUniqueId(), Iso19103Constants.FQ_STEREOTYPE_PROPERTY)
        && stereotypes.size() == 1;
  }

}
