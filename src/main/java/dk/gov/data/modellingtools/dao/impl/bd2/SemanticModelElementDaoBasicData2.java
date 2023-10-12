package dk.gov.data.modellingtools.dao.impl.bd2;

import dk.gov.data.modellingtools.dao.impl.AbstractSemanticModelElementDao;
import dk.gov.data.modellingtools.dao.impl.EaConceptDaoForLogicalDataModel;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import org.apache.commons.collections4.MultiValuedMap;
import org.sparx.Attribute;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

/**
 * Implementation of {@link AbstractSemanticModelElementDao} for the Basic Data modelling rules
 * version 2.
 */
public class SemanticModelElementDaoBasicData2 extends AbstractSemanticModelElementDao {

  private EaConceptDaoForLogicalDataModel eaConceptDao;

  public SemanticModelElementDaoBasicData2(EnterpriseArchitectWrapper enterpriseArchitectWrapper) {
    super(enterpriseArchitectWrapper);
    this.eaConceptDao = new ConceptDaoBasicData2();
  }

  @Override
  protected Concept createConcept(Element element) throws ModellingToolsException {
    return eaConceptDao.findByElement(element);
  }

  @Override
  protected Concept createConcept(Attribute attribute) throws ModellingToolsException {
    return eaConceptDao.findByAttribute(attribute);
  }

  @Override
  protected Concept createConcept(ConnectorEnd oppositeConnectorEnd)
      throws ModellingToolsException {
    return eaConceptDao.findByConnectorEnd(oppositeConnectorEnd);
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Element element,
      MultiValuedMap<String, String> elementFqStereotypes) {
    return BasicData2DaoUtils.isRelevantModelElement(element, elementFqStereotypes);
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    return BasicData2DaoUtils.isRelevantModelElement(attribute, attributeFqStereotypes);
  }

  @Override
  protected boolean qualifiesOppositeEndAsSemanticModelElement(
      EaConnectorEnd eaConnectorEnd, MultiValuedMap<String, String> connectorEndFqStereotypes) {
    return BasicData2DaoUtils.isOppositeEndRelevantModelElement(eaConnectorEnd,
        connectorEndFqStereotypes);
  }

}
