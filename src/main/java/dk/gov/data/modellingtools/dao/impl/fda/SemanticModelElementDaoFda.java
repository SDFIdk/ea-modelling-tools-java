package dk.gov.data.modellingtools.dao.impl.fda;

import dk.gov.data.modellingtools.constants.FdaConstants;
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
  protected boolean qualifiesAsSemanticModelElement(Element element) {
    return element.HasStereotype(FdaConstants.FQ_STEREOTYPE_MODEL_ELEMENT);
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    return attributeFqStereotypes.containsMapping(attribute.GetAttributeGUID(),
        FdaConstants.FQ_STEREOTYPE_MODEL_ELEMENT);
  }

  @Override
  protected boolean qualifiesOppositeEndAsSemanticModelElement(
      MultiValuedMap<String, String> connectorEndFqStereotypes, EaConnectorEnd eaConnectorEnd) {
    return connectorEndFqStereotypes.containsMapping(eaConnectorEnd.getConnectorEndUniqueId(),
        FdaConstants.FQ_STEREOTYPE_MODEL_ELEMENT);
  }

}
