package dk.gov.data.modellingtools.dao.impl.bd1;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.dao.impl.AbstractSemanticModelElementDao;
import dk.gov.data.modellingtools.dao.impl.EaConceptDaoForLogicalDataModel;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections4.MultiValuedMap;
import org.sparx.Attribute;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

/**
 * Implementation of {@link AbstractSemanticModelElementDao} for the Basic Data modelling rules
 * version 1.
 */
public class SemanticModelElementDaoBasicData1 extends AbstractSemanticModelElementDao {

  private EaConceptDaoForLogicalDataModel eaConceptDao;

  public SemanticModelElementDaoBasicData1(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
    this.eaConceptDao = new ConceptDaoBasicData1();
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public SemanticModelElementDaoBasicData1(EnterpriseArchitectWrapper eaWrapper,
      EaConceptDaoForLogicalDataModel eaConceptDao) {
    super(eaWrapper);
    this.eaConceptDao = eaConceptDao;
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Element element) {
    return element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_OBJECT_TYPE)
        || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_FEATURE_TYPE)
        || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_ENUMERATION)
        || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_CODE_LIST)
        || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_DATA_TYPE);
  }

  @Override
  protected boolean qualifiesAsSemanticModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    return attributeFqStereotypes.containsMapping(attribute.GetAttributeGUID(),
        BasicData1Constants.FQ_STEREOTYPE_PROPERTY);
  }

  @Override
  protected boolean qualifiesOppositeEndAsSemanticModelElement(
      MultiValuedMap<String, String> connectorEndFqStereotypes, EaConnectorEnd eaConnectorEnd) {
    return connectorEndFqStereotypes.containsMapping(eaConnectorEnd.getConnectorEndUniqueId(),
        BasicData1Constants.FQ_STEREOTYPE_PROPERTY);
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

}
