package dk.gov.data.modellingtools.dao.impl.bd1;

import dk.gov.data.modellingtools.constants.BasicData1Constants;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import java.util.Collection;
import org.apache.commons.collections4.MultiValuedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.Element;

final class BasicData1DaoUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicData1DaoUtils.class);

  private BasicData1DaoUtils() {
    super();
  }

  static boolean isRelevantModelElement(Element element,
      MultiValuedMap<String, String> elementFqStereotypes) {
    Collection<String> stereotypes = elementFqStereotypes.get(element.GetElementGUID());
    if (stereotypes.size() > 1) {
      LOGGER.warn("{} has more than one stereotype: {}", EaModelUtils.toString(element),
          stereotypes);
    }
    return EaModelUtils.getDataModellingClassifiers().contains(element.GetType())
        && (element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_OBJECT_TYPE)
            || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_FEATURE_TYPE)
            || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_ENUMERATION)
            || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_CODE_LIST)
            || element.HasStereotype(BasicData1Constants.FQ_STEREOTYPE_DATA_TYPE))
        && stereotypes.size() == 1;
  }

  static boolean isRelevantModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    Collection<String> stereotypes = attributeFqStereotypes.get(attribute.GetAttributeGUID());
    if (stereotypes.size() > 1) {
      LOGGER.warn("{} has more than one stereotype: {}", EaModelUtils.toString(attribute),
          stereotypes);
    }
    return attributeFqStereotypes.containsMapping(attribute.GetAttributeGUID(),
        BasicData1Constants.FQ_STEREOTYPE_PROPERTY) && stereotypes.size() == 1;
  }

  static boolean isOppositeEndRelevantModelElement(EaConnectorEnd eaConnectorEnd,
      MultiValuedMap<String, String> connectorEndFqStereotypes) {
    Collection<String> stereotypes =
        connectorEndFqStereotypes.get(eaConnectorEnd.getOppositeConnectorEndUniqueId());
    if (stereotypes.size() > 1) {
      LOGGER.warn("{} has more than one stereotype: {}",
          EaModelUtils.toString(eaConnectorEnd.getOppositeConnectorEnd()), stereotypes);
    }
    return connectorEndFqStereotypes.containsMapping(
        eaConnectorEnd.getOppositeConnectorEndUniqueId(),
        BasicData1Constants.FQ_STEREOTYPE_PROPERTY) && stereotypes.size() == 1;
  }



}
