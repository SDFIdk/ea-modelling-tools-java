package dk.gov.data.modellingtools.ea.utils;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.model.ConnectorType;
import dk.gov.data.modellingtools.ea.model.EaConnectorEnd;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.ModelElement;
import dk.gov.data.modellingtools.model.ModelElement.ModelElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

  /*
   * When functionality from the EA Repository is needed, then add a method in
   * EnterpriseArchitectWrapper instead of adding a method here with an EnterpriseArchitectWrapper
   * as a parameter.
   */

  private EaModelUtils() {}

  /**
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
    } else {
      throw new UnsupportedOperationException(
          "Unexpected type of " + toString(element) + ": " + element.GetType());
    }
    return umlModelElementType;
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
   * @return associations (including the aggregations and compositions) in the given package or one
   *         of its subpackages. This method does not take into account that a connector may be
   *         version controlled in another package
   */
  public static Collection<Connector> getAllAssociationsOfPackageAndSubpackages(
      Package umlPackage) {
    Collection<Element> elements = getElementsOfPackageAndSubpackages(umlPackage);
    Map<String, Connector> connectors = new HashMap<>();

    for (Element element : elements) {
      for (Connector connector : element.GetConnectors()) {
        if (!connectors.containsKey(connector.GetConnectorGUID())) {
          connectors.put(null, connector);
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
      org.sparx.Collection<T> eACollection) {
    Collection<T> javaCollection = new ArrayList<>();
    for (T item : eACollection) {
      javaCollection.add(item);
    }
    return javaCollection;
  }

  public static boolean hasPackageStereotype(Package umlPackage, String stereotype) {
    List<String> packageStereotypes =
        Arrays.asList(StringUtils.split(umlPackage.GetStereotypeEx(), ','));
    return packageStereotypes.contains(stereotype);
  }

  /**
   * Returns those connector ends that involve the given element and that are part of an
   * association, aggregation or composition.
   */
  public static Collection<EaConnectorEnd> getAssocationConnectorEnds(Element element) {
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
    } else { // TODO add more types
      throw new UnsupportedOperationException(
          "Cannot call this method for object of type " + object.getClass());
    }
  }

}
