package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import org.apache.commons.collections4.MultiValuedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

/**
 * Data Access Object for semantic model elements modelled using the FDA rules described at
 * https://arkitektur.digst.dk/en/metoder/regler-begrebs-og-datamodellering.
 */
public class SemanticModelElementDaoFda extends AbstractSemanticModelElementDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(SemanticModelElementDaoFda.class);

  private ConceptDaoFda conceptDaoFda;

  public SemanticModelElementDaoFda(EnterpriseArchitectWrapper enterpriseArchitectWrapper) {
    super(enterpriseArchitectWrapper);
    this.conceptDaoFda = new ConceptDaoFda();
  }

  @Override
  protected Concept createConcept(Element element) throws ModellingToolsException {
    return conceptDaoFda.findByElement(element);
  }

  @Override
  protected Concept createConcept(Attribute attribute) throws ModellingToolsException {
    return conceptDaoFda.findByAttribute(attribute);
  }

  @Override
  protected Concept createConcept(ConnectorEnd connectorEnd) throws ModellingToolsException {
    return conceptDaoFda.findByConnectorEnd(connectorEnd);
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
