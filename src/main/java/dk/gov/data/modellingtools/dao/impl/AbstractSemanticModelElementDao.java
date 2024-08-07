package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.SemanticModelElementDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import dk.gov.data.modellingtools.model.ModelElement;
import dk.gov.data.modellingtools.model.SemanticModelElement;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.ConnectorEnd;
import org.sparx.Element;
import org.sparx.Package;

/**
 * Gathers common logic for {@link SemanticModelElementDao}s.
 */
public abstract class AbstractSemanticModelElementDao implements SemanticModelElementDao {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractSemanticModelElementDao.class);

  private EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public AbstractSemanticModelElementDao(EnterpriseArchitectWrapper enterpriseArchitectWrapper) {
    this.eaWrapper = enterpriseArchitectWrapper;
  }

  @Override
  public final List<SemanticModelElement> findAllByPackageGuid(String packageGuid)
      throws ModellingToolsException {
    Package umlPackage = eaWrapper.getPackageByGuid(packageGuid);
    LOGGER.debug("Finding semantic model elements in {}", EaModelUtils.toString(umlPackage));
    /*
     * Retrieving fully-qualified stereotypes of attributes, connector ends, etc. from the EA model
     * file is time-expensive, so retrieving them all at once for the package.
     */
    MultiValuedMap<String, String> elementFqStereotypes =
        this.eaWrapper.retrieveElementFqStereotypes(umlPackage);
    MultiValuedMap<String, String> attributeFqStereotypes =
        this.eaWrapper.retrieveAttributeFqStereotypes(umlPackage);
    MultiValuedMap<String, String> connectorEndFqStereotypes =
        this.eaWrapper.retrieveConnectorEndFqStereotypes();
    List<SemanticModelElement> allSemanticModelElements = new ArrayList<>();
    for (Element element : EaModelUtils.getElementsOfPackageAndSubpackages(umlPackage)) {
      if (qualifiesAsSemanticModelElement(element, elementFqStereotypes)) {
        allSemanticModelElements.add(createSemanticModelElement(element));
        allSemanticModelElements
            .addAll(findSemanticModelElementsOnElementAttributes(element, attributeFqStereotypes));
        allSemanticModelElements.addAll(findSemanticModelElementsOnNavigableAssociationEnds(element,
            connectorEndFqStereotypes));
      } else {
        LOGGER.info("Skipping {}", EaModelUtils.toString(element));
      }
    }
    LOGGER.info("Found {} semantic model elements in total", allSemanticModelElements.size());
    return allSemanticModelElements;
  }

  private List<SemanticModelElement> findSemanticModelElementsOnElementAttributes(Element element,
      MultiValuedMap<String, String> attributeFqStereotypes) throws ModellingToolsException {
    List<SemanticModelElement> semanticModelElements = new ArrayList<>();
    for (Attribute attribute : element.GetAttributes()) {
      if (qualifiesAsSemanticModelElement(attribute, attributeFqStereotypes)) {
        semanticModelElements.add(createSemanticModelElement(attribute));
      } else {
        LOGGER.warn("Skipping {}", EaModelUtils.toString(attribute));
      }
    }
    return semanticModelElements;
  }

  private List<SemanticModelElement> findSemanticModelElementsOnNavigableAssociationEnds(
      Element element, MultiValuedMap<String, String> connectorEndFqStereotypes)
      throws ModellingToolsException {
    List<SemanticModelElement> semanticModelElements = new ArrayList<>();
    Collection<EaConnectorEnd> associationConnectorEnds =
        EaModelUtils.getAssociationConnectorEnds(element);
    for (EaConnectorEnd eaConnectorEnd : associationConnectorEnds) {
      if (eaConnectorEnd.getOppositeConnectorEnd().GetIsNavigable()) {
        if (qualifiesOppositeEndAsSemanticModelElement(eaConnectorEnd, connectorEndFqStereotypes)) {
          semanticModelElements
              .add(createSemanticModelElement(eaConnectorEnd.getOppositeConnectorEnd(),
                  eaConnectorEnd.getOppositeConnectorEndUniqueId()));
        } else {
          LOGGER.warn("Skipping the opposite end of {}", eaConnectorEnd);
        }
      }
    }
    return semanticModelElements;
  }

  protected abstract boolean qualifiesOppositeEndAsSemanticModelElement(
      EaConnectorEnd eaConnectorEnd, MultiValuedMap<String, String> connectorEndFqStereotypes);

  protected abstract boolean qualifiesAsSemanticModelElement(Element element,
      MultiValuedMap<String, String> elementFqStereotypes);

  protected abstract boolean qualifiesAsSemanticModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes);

  private SemanticModelElement createSemanticModelElement(Element element)
      throws ModellingToolsException {
    SemanticModelElement semanticModelElement = new SemanticModelElement();
    semanticModelElement.setEaGuid(element.GetElementGUID());
    semanticModelElement.setUmlName(element.GetName());
    semanticModelElement.setUmlModelElementType(EaModelUtils.determineUmlModelElementType(element));
    semanticModelElement.setConcept(createConcept(element));
    return semanticModelElement;
  }

  private SemanticModelElement createSemanticModelElement(Attribute attribute)
      throws ModellingToolsException {
    SemanticModelElement semanticModelElement = new SemanticModelElement();
    semanticModelElement.setEaGuid(attribute.GetAttributeGUID());
    semanticModelElement.setUmlName(attribute.GetName());
    semanticModelElement
        .setUmlModelElementType(EaModelUtils.determineUmlModelElementType(attribute));
    semanticModelElement.setConcept(createConcept(attribute));
    return semanticModelElement;
  }

  private SemanticModelElement createSemanticModelElement(ConnectorEnd oppositeConnectorEnd,
      String id) throws ModellingToolsException {
    SemanticModelElement semanticModelElement = new SemanticModelElement();
    semanticModelElement.setEaGuid(id);
    semanticModelElement.setUmlName(oppositeConnectorEnd.GetRole());
    semanticModelElement.setUmlModelElementType(ModelElement.ModelElementType.ASSOCIATION_END);
    semanticModelElement.setConcept(createConcept(oppositeConnectorEnd));
    return semanticModelElement;
  }

  protected abstract Concept createConcept(Element element) throws ModellingToolsException;

  protected abstract Concept createConcept(Attribute attribute) throws ModellingToolsException;

  protected abstract Concept createConcept(ConnectorEnd oppositeConnectorEnd)
      throws ModellingToolsException;

}
