package dk.gov.data.modellingtools.ea.utils;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.ConnectorType;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.ModelElement;
import dk.gov.data.modellingtools.model.ModelElement.ModelElementType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Diagram;
import org.sparx.Element;
import org.sparx.Package;

/**
 * Utility methods for working with EA models.
 *
 * @see EnterpriseArchitectWrapper
 */
public final class EaModelUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(EaModelUtils.class);

  /*
   * When functionality from the EA Repository is needed, then add a method in
   * EnterpriseArchitectWrapper instead of adding a method here with an EnterpriseArchitectWrapper
   * as a parameter.
   */

  private EaModelUtils() {}

  private static final Collection<String> dataModellingClassifiers =
      Set.of("Class", "DataType", "Enumeration", "Interface");

  private static final Collection<String> dataModellingClassifiersWithAttributes =
      Set.of("Class", "DataType", "Interface");

  /**
   * Determines the type of the given element.
   *
   * @throws UnsupportedOperationException if the element type is unknown to this application
   */
  public static ModelElementType determineUmlModelElementType(Element element)
      throws ModellingToolsException {
    ModelElementType umlModelElementType;
    if (ModelElement.ModelElementType.CLASS.getEaType().equals(element.GetType())) {
      umlModelElementType = ModelElement.ModelElementType.CLASS;
    } else if (ModelElement.ModelElementType.DATA_TYPE.getEaType().equals(element.GetType())) {
      umlModelElementType = ModelElement.ModelElementType.DATA_TYPE;
    } else if (ModelElement.ModelElementType.ENUMERATION.getEaType().equals(element.GetType())) {
      umlModelElementType = ModelElement.ModelElementType.ENUMERATION;
    } else if (ModelElement.ModelElementType.INTERFACE.getEaType().equals(element.GetType())) {
      umlModelElementType = ModelElement.ModelElementType.INTERFACE;
    } else {
      throw new UnsupportedOperationException(
          "Unexpected type of " + toString(element) + ": " + element.GetType());
    }
    return umlModelElementType;
  }

  /**
   * Determines the type of the given attribute. Both UML attributes and UML enumeration literals
   * are represented by {@link Attribute}.
   */
  public static ModelElementType determineUmlModelElementType(Attribute attribute) {
    ModelElementType umlModelElementType;
    if (attribute.GetStyleEx().contains("IsLiteral=1")) {
      umlModelElementType = ModelElement.ModelElementType.ENUMERATION_LITERAL;
    } else {
      umlModelElementType = ModelElement.ModelElementType.ATTRIBUTE;
    }
    return umlModelElementType;
  }

  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Collection<String> getDataModellingClassifiers() {
    return dataModellingClassifiers;
  }

  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Collection<String> getDataModellingClassifiersWithAttributes() {
    return dataModellingClassifiersWithAttributes;
  }

  /**
   * Gets the {@link Element}s in the given package and its subpackages (recursively).
   */
  public static Collection<Element> getElementsOfPackageAndSubpackages(Package umlPackage) {
    Collection<Element> elements = new ArrayList<>();
    elements.addAll(convertEaCollectionToJavaCollection(umlPackage.GetElements()));
    for (Package subPackage : umlPackage.GetPackages()) {
      elements.addAll(getElementsOfPackageAndSubpackages(subPackage));
    }
    return Collections.unmodifiableCollection(new ArrayList<>(elements));
  }

  /**
   * Gets the {@link Diagram}s in the given package and its subpackages (recursively).
   */
  public static Collection<Diagram> getDiagramsOfPackageAndSubpackages(Package umlPackage) {
    Collection<Diagram> diagrams = new ArrayList<>();
    diagrams.addAll(convertEaCollectionToJavaCollection(umlPackage.GetDiagrams()));
    for (Package subPackage : umlPackage.GetPackages()) {
      diagrams.addAll(getDiagramsOfPackageAndSubpackages(subPackage));
    }
    return Collections.unmodifiableCollection(new ArrayList<>(diagrams));
  }

  /**
   * Gets the {@link Package}s in the given package and all their subpackages (recursively).
   */
  public static Collection<Package> getSubpackages(Package umlPackage) {
    Collection<Package> subpackages = new ArrayList<>();
    Collection<Package> convertedCollection =
        convertEaCollectionToJavaCollection(umlPackage.GetPackages());
    subpackages.addAll(convertedCollection);
    for (Package subpackage : convertedCollection) {
      subpackages.addAll(getSubpackages(subpackage));
    }
    return Collections.unmodifiableCollection(new ArrayList<>(subpackages));
  }

  /**
   * Gets. associations (including the aggregations and compositions) in the given package or one of
   * its subpackages.
   * 
   * <p>This method does not take into account that a connector may be version controlled in another
   * package.</p>
   */
  public static Collection<Connector> getAllAssociationsOfPackageAndSubpackages(
      Package umlPackage) {
    Collection<Element> elements = getElementsOfPackageAndSubpackages(umlPackage);
    Map<String, Connector> connectors = new HashMap<>();

    for (Element element : elements) {
      for (Connector connector : element.GetConnectors()) {
        if (("Association".equals(connector.GetType()) || "Aggregation".equals(connector.GetType()))
            && !connectors.containsKey(connector.GetConnectorGUID())) {
          connectors.put(connector.GetConnectorGUID(), connector);
        }
      }
    }
    return Collections.unmodifiableCollection(connectors.values());
  }

  /**
   * Puts the elements of a {@link org.sparx.Collection} in a {@link Collection}.
   *
   * @param <T> Sparx type
   */
  public static <T> Collection<T> convertEaCollectionToJavaCollection(
      org.sparx.Collection<T> eaCollection) {
    Collection<T> javaCollection = new ArrayList<>();
    for (T item : eaCollection) {
      javaCollection.add(item);
    }
    return javaCollection;
  }

  /**
   * Checks whether the given package has the given stereotype.
   */
  public static boolean hasPackageStereotype(Package umlPackage, String stereotype) {
    String stereotypeEx = umlPackage.GetStereotypeEx();
    LOGGER.debug("Stereotypes on package {}: {}", toString(umlPackage), stereotypeEx);
    LOGGER.debug("Stereotypes on element {}: {}", toString(umlPackage.GetElement()),
        umlPackage.GetElement().GetFQStereotype());
    List<String> packageStereotypes = Arrays.asList(StringUtils.split(stereotypeEx, ','));
    return packageStereotypes.contains(stereotype);
  }

  /**
   * Returns those connector ends that involve the given element and that are part of an
   * association, aggregation or composition.
   */
  public static Collection<EaConnectorEnd> getAssociationConnectorEnds(Element element) {
    return CollectionUtils.select(EaConnectorEnd.createConnectorEnds(element),
        new Predicate<EaConnectorEnd>() {

          @Override
          public boolean evaluate(EaConnectorEnd eaConnectorEnd) {
            return ConnectorType.ASSOCIATION.equals(eaConnectorEnd.getConnectorType())
                || ConnectorType.AGGREGATION.equals(eaConnectorEnd.getConnectorType());
          }
        });
  }


  /**
   * Returns a string containing the name and GUID of the given package, e.g. for use in logging.
   */
  public static String toString(Package umlPackage) {
    return new ToStringBuilder(umlPackage).append("name", umlPackage.GetName())
        .append("GUID", umlPackage.GetPackageGUID()).toString();
  }

  /**
   * Returns a string containing the name and GUID of the given element, e.g. for use in logging.
   */
  public static String toString(Element element) {
    return new ToStringBuilder(element).append("name", element.GetName())
        .append("GUID", element.GetElementGUID()).toString();
  }

  /**
   * Returns a string containing the name and GUID of the given attribute, e.g. for use in logging.
   */
  public static String toString(Attribute attribute) {
    return new ToStringBuilder(attribute).append("name", attribute.GetName())
        .append("GUID", attribute.GetAttributeGUID()).toString();
  }

  /**
   * Returns a string containing the name and GUID of the given connector, e.g. for use in logging.
   */
  public static String toString(Connector connector) {
    return new ToStringBuilder(connector).append("connector name", connector.GetName())
        .append("connector GUID", connector.GetConnectorGUID()).toString();
  }

  /**
   * Returns a string containing the name and GUID of the given connector, e.g. for use in logging.
   */
  public static String toString(ConnectorEnd connectorEnd) {
    return new ToStringBuilder(connectorEnd).append("name", connectorEnd.GetRole()).toString();
  }

  /**
   * Returns a string containing the name and GUID of the given object, e.g. for use in logging.
   *
   * @throws UnsupportedOperationException if the type of object is unknown to this application
   */
  public static String toString(Object object) {
    if (object instanceof Element) {
      return toString((Element) object);
    } else if (object instanceof Package) {
      return toString((Package) object);
    } else if (object instanceof Attribute) {
      return toString((Attribute) object);
    } else if (object instanceof ConnectorEnd) {
      return toString((ConnectorEnd) object);
    } else if (object instanceof Connector) {
      return toString((Connector) object);
    } else { // TODO add more types
      throw new UnsupportedOperationException(
          "Cannot call this method for object of type " + object.getClass());
    }
  }

  /**
   * Find the connector end with the given GUID via the collection of associations.
   */
  public static ConnectorEnd findConnectorEnd(Collection<Connector> associations,
      String connectorEndGuid) throws ModellingToolsException {
    LOGGER.trace("Trying to find connector GUID that matches {} in {}", connectorEndGuid,
        associations);
    Connector association = IterableUtils.find(associations, new Predicate<Connector>() {

      @Override
      public boolean evaluate(Connector connector) {
        // see also class EaConnectorEnd for more information about the logic
        // example of connector GUID: {0D467508-0484-4a26-B670-7A54ABB7F73D}
        // example of connector end GUID: {src467508-0484-4a26-B670-7A54ABB7F73D}
        String lastPartConnectorGuid = connector.GetConnectorGUID().substring(3);
        String lastPartConnectorEndGuid = connectorEndGuid.substring(4);
        LOGGER.trace("Comparing {} to {}", lastPartConnectorGuid, lastPartConnectorEndGuid);
        return lastPartConnectorGuid.equals(lastPartConnectorEndGuid);
      }
    });
    if (association == null) {
      throw new ModellingToolsException(
          "No association found with a connector GUID that matches " + connectorEndGuid);
    }
    ConnectorEnd connectorEnd;
    // see also class EaConnectorEnd for more information about the logic
    // example of connectorEndGuid: {src467508-0484-4a26-B670-7A54ABB7F73D}
    switch (connectorEndGuid.substring(1, 4)) {
      case EaConnectorEnd.ID_PREFIX_SOURCE:
        connectorEnd = association.GetClientEnd();
        break;
      case EaConnectorEnd.ID_PREFIX_TARGET:
        connectorEnd = association.GetSupplierEnd();
        break;
      default:
        throw new ModellingToolsException(
            "Unknown prefix in GUID " + connectorEndGuid + ": " + connectorEndGuid.substring(0, 4));
    }
    LOGGER.debug("Found {} and {}", EaModelUtils.toString(association),
        EaModelUtils.toString(connectorEnd));
    return connectorEnd;
  }

}
