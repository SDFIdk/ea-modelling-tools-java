package dk.gov.data.modellingtools.dao.impl.fda;

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
 * Data Access Object for semantic model elements modelled using the FDA rules described at
 * https://arkitektur.digst.dk/en/metoder/regler-begrebs-og-datamodellering.
 */
public class SemanticModelElementDaoFda extends AbstractSemanticModelElementDao {

  private EaConceptDaoForLogicalDataModel eaConceptDao;

  public SemanticModelElementDaoFda(EnterpriseArchitectWrapper enterpriseArchitectWrapper) {
    super(enterpriseArchitectWrapper);
    this.eaConceptDao = new ConceptDaoFdaForLogicalDataModel();
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
  protected Concept createConcept(ConnectorEnd connectorEnd) throws ModellingToolsException {
    return eaConceptDao.findByConnectorEnd(connectorEnd);
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Element element,
      MultiValuedMap<String, String> elementFqStereotypes) {
    return FdaDaoUtils.isRelevantModelElement(element, elementFqStereotypes);
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    return FdaDaoUtils.isRelevantModelElement(attribute, attributeFqStereotypes);
  }

  @Override
  protected boolean qualifiesOppositeEndAsSemanticModelElement(EaConnectorEnd eaConnectorEnd,
      MultiValuedMap<String, String> connectorEndFqStereotypes) {
    return FdaDaoUtils.isRelevantModelElement(eaConnectorEnd, connectorEndFqStereotypes);
  }

}
