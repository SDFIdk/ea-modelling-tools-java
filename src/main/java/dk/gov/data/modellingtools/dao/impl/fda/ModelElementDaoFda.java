package dk.gov.data.modellingtools.dao.impl.fda;

import dk.gov.data.modellingtools.dao.ModelElementDao;
import dk.gov.data.modellingtools.dao.impl.AbstractModelElementDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import org.apache.commons.collections4.MultiValuedMap;
import org.sparx.Attribute;
import org.sparx.Element;

/**
 * Implementation of {@link AbstractModelElementDao} for the FDA modelling rules.
 */
public class ModelElementDaoFda extends AbstractModelElementDao implements ModelElementDao {

  public ModelElementDaoFda(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected boolean isRelevantModelElement(Element element,
      MultiValuedMap<String, String> elementFqStereotypes) {
    return FdaDaoUtils.isRelevantModelElement(element, elementFqStereotypes);
  }

  @Override
  protected boolean isRelevantModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    return FdaDaoUtils.isRelevantModelElement(attribute, attributeFqStereotypes);
  }

  @Override
  protected boolean isOppositeConnectorEndRelevantModelElement(EaConnectorEnd eaConnectorEnd,
      MultiValuedMap<String, String> connectorEndFqStereotypes) {
    return FdaDaoUtils.isRelevantModelElement(eaConnectorEnd, connectorEndFqStereotypes);
  }

}
