package dk.gov.data.modellingtools.dao.impl.bd2;

import dk.gov.data.modellingtools.dao.impl.AbstractModelElementDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import org.apache.commons.collections4.MultiValuedMap;
import org.sparx.Attribute;
import org.sparx.Element;

/**
 * Implementation of {@link AbstractModelElementDao} for the Basic Data modelling rules version 2.
 */
public class ModelElementDaoBasicData2 extends AbstractModelElementDao {

  public ModelElementDaoBasicData2(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected boolean isRelevantModelElement(Element element,
      MultiValuedMap<String, String> elementFqStereotypes) {
    return BasicData2DaoUtils.isRelevantModelElement(element, elementFqStereotypes);
  }

  @Override
  protected boolean isRelevantModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    return BasicData2DaoUtils.isRelevantModelElement(attribute, attributeFqStereotypes);
  }

  @Override
  protected boolean isOppositeConnectorEndRelevantModelElement(EaConnectorEnd eaConnectorEnd,
      MultiValuedMap<String, String> connectorEndFqStereotypes) {
    return BasicData2DaoUtils.isOppositeEndRelevantModelElement(eaConnectorEnd,
        connectorEndFqStereotypes);
  }

}
