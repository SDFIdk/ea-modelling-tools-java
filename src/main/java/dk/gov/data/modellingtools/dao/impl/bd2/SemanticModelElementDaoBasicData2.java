package dk.gov.data.modellingtools.dao.impl.bd2;

import dk.gov.data.modellingtools.constants.BasicData2Constants;
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
  protected boolean qualifiesAsSemanticModelElement(Element element) {
    return element.HasStereotype(BasicData2Constants.FQ_STEREOTYPE_CODE_LIST)
        || element.HasStereotype(BasicData2Constants.FQ_STEREOTYPE_DATA_TYPE)
        || element.HasStereotype(BasicData2Constants.FQ_STEREOTYPE_ENUMERATION)
        || element.HasStereotype(BasicData2Constants.FQ_STEREOTYPE_OBJECT_TYPE);
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    return attributeFqStereotypes.containsMapping(attribute.GetAttributeGUID(),
        BasicData2Constants.FQ_STEREOTYPE_PROPERTY)
        || attributeFqStereotypes.containsMapping(attribute.GetAttributeGUID(),
            BasicData2Constants.FQ_STEREOTYPE_ENUMERATION_LITERAL);
  }

  @Override
  protected boolean qualifiesOppositeEndAsSemanticModelElement(
      MultiValuedMap<String, String> connectorEndFqStereotypes, EaConnectorEnd eaConnectorEnd) {
    return connectorEndFqStereotypes.containsMapping(eaConnectorEnd.getConnectorEndUniqueId(),
        BasicData2Constants.FQ_STEREOTYPE_PROPERTY);
  }

}
