package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.ModelElementDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.ModelElement;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.Element;
import org.sparx.Package;

/**
 * {@link AbstractEaConceptDao} assumes that a relevant model item has one stereotype.
 */
public abstract class AbstractModelElementDao implements ModelElementDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelElementDao.class);

  private EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public AbstractModelElementDao(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public Collection<ModelElement> findAllByPackageGuid(String packageGuid)
      throws ModellingToolsException {
    List<ModelElement> modelElements = new ArrayList<>();
    Package umlPackage = eaWrapper.getPackageByGuid(packageGuid);
    LOGGER.info("Finding model elements in package {}", EaModelUtils.toString(umlPackage));

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

    for (Element element : EaModelUtils.getElementsOfPackageAndSubpackages(umlPackage)) {
      if (isRelevantModelElement(element, elementFqStereotypes)) {
        LOGGER.debug("Finding model elements equal to or related to {}",
            EaModelUtils.toString(element));
        modelElements.add(createModelElement(umlPackage, element));
        for (Attribute attribute : element.GetAttributes()) {
          if (isRelevantModelElement(attribute, attributeFqStereotypes)) {
            modelElements.add(createModelElement(element, attribute));
          }
        }
        Collection<EaConnectorEnd> assocationConnectorEnds =
            EaModelUtils.getAssocationConnectorEnds(element);
        for (EaConnectorEnd eaConnectorEnd : assocationConnectorEnds) {
          if (isOppositeConnectorEndRelevantModelElement(eaConnectorEnd,
              connectorEndFqStereotypes)) {
            modelElements.add(createModelElementForOppositeConnectorEnd(element, eaConnectorEnd,
                connectorEndFqStereotypes));
          }
        }
      }
    }
    LOGGER.info("Found {} relevant model elements in package {}", modelElements.size(),
        EaModelUtils.toString(umlPackage));
    return modelElements;
  }

  private ModelElement createModelElement(Package umlPackage, Element element)
      throws ModellingToolsException {
    ModelElement modelElement = new ModelElement();
    modelElement.setEaGuid(element.GetElementGUID());
    modelElement.setUmlName(element.GetName());
    modelElement.setUniqueName(element.GetName());
    modelElement.setNamespaceName(umlPackage.GetName());
    modelElement.setUmlModelElementType(EaModelUtils.determineUmlModelElementType(element));
    modelElement.setFqStereotype(element.GetFQStereotype());
    modelElement.setTaggedValues(TaggedValueUtils.getTaggedValues(element));
    return modelElement;
  }

  private ModelElement createModelElement(Element element, Attribute attribute) {
    ModelElement modelElement = new ModelElement();
    modelElement.setEaGuid(attribute.GetAttributeGUID());
    modelElement.setUmlName(attribute.GetName());
    modelElement.setUniqueName(String.join("::", element.GetName(), attribute.GetName()));
    modelElement.setNamespaceName(element.GetName());
    modelElement.setUmlModelElementType(EaModelUtils.determineUmlModelElementType(attribute));
    modelElement.setFqStereotype(attribute.GetFQStereotype());
    modelElement.setTaggedValues(TaggedValueUtils.getTaggedValues(attribute));
    return modelElement;
  }

  private ModelElement createModelElementForOppositeConnectorEnd(Element element,
      EaConnectorEnd eaConnectorEnd, MultiValuedMap<String, String> connectorEndFqStereotypes) {
    ModelElement modelElement = new ModelElement();
    modelElement.setEaGuid(eaConnectorEnd.getOppositeConnectorEndUniqueId());
    modelElement.setUmlName(eaConnectorEnd.getOppositeConnectorEnd().GetRole());
    modelElement.setUniqueName(
        String.join("::", element.GetName(), eaConnectorEnd.getOppositeConnectorEnd().GetRole()));
    modelElement.setNamespaceName(element.GetName());
    modelElement.setUmlModelElementType(ModelElement.ModelElementType.ASSOCIATION_END);
    Iterator<String> stereotypeIterator =
        connectorEndFqStereotypes.get(eaConnectorEnd.getOppositeConnectorEndUniqueId()).iterator();
    if (stereotypeIterator.hasNext()) {
      modelElement.setFqStereotype(stereotypeIterator.next());
    }
    modelElement.setTaggedValues(
        TaggedValueUtils.getTaggedValues(eaConnectorEnd.getOppositeConnectorEnd()));
    return modelElement;
  }

  protected abstract boolean isRelevantModelElement(Element element,
      MultiValuedMap<String, String> elementFqStereotypes);

  protected abstract boolean isRelevantModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes);

  protected abstract boolean isOppositeConnectorEndRelevantModelElement(
      EaConnectorEnd eaConnectorEnd, MultiValuedMap<String, String> connectorEndFqStereotypes);

}
