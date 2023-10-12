package dk.gov.data.modellingtools.dao.impl.fda;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import org.apache.commons.collections4.MultiValuedMap;
import org.sparx.Attribute;
import org.sparx.Element;

final class FdaDaoUtils {

  private FdaDaoUtils() {
    super();
  }

  static boolean isRelevantModelElement(Element element,
      MultiValuedMap<String, String> elementFqStereotypes) {
    return element.HasStereotype(FdaConstants.FQ_STEREOTYPE_MODEL_ELEMENT);
  }

  static boolean isRelevantModelElement(Attribute attribute,
      MultiValuedMap<String, String> attributeFqStereotypes) {
    return attributeFqStereotypes.containsMapping(attribute.GetAttributeGUID(),
        FdaConstants.FQ_STEREOTYPE_MODEL_ELEMENT);
  }

  static boolean isRelevantModelElement(EaConnectorEnd eaConnectorEnd,
      MultiValuedMap<String, String> connectorEndFqStereotypes) {
    return connectorEndFqStereotypes.containsMapping(eaConnectorEnd.getConnectorEndUniqueId(),
        FdaConstants.FQ_STEREOTYPE_MODEL_ELEMENT);
  }

}
