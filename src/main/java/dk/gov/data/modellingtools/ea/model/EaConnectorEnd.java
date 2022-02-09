package dk.gov.data.modellingtools.ea.model;

import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

/**
 * Class to make dealing with connector ends easier, as {@link ConnectorEnd} does not have methods
 * to retrieve its element nor its connector.
 */
public final class EaConnectorEnd {

  public static final String ID_SUFFIX_SOURCE =
      ConnectorEndType.SOURCE.toString().toLowerCase(Locale.ENGLISH);
  public static final String ID_SUFFIX_TARGET =
      ConnectorEndType.TARGET.toString().toLowerCase(Locale.ENGLISH);

  private Connector connector;
  private ConnectorEnd connectorEnd;
  private Element element;
  private int oppositeElementId;
  private ConnectorEnd oppositeConnectorEnd;
  private ConnectorType connectorType;
  private ConnectorEndType connectorEndType;

  private EaConnectorEnd(Connector connector, ConnectorType connectorType,
      ConnectorEnd connectorEnd, ConnectorEndType connectorEndType, Element element,
      ConnectorEnd oppositeConnectorEnd, int oppositeElementId) {
    super();
    this.element = element;
    this.oppositeElementId = oppositeElementId;
    this.connector = connector;
    this.connectorEnd = connectorEnd;
    this.oppositeConnectorEnd = oppositeConnectorEnd;
    this.connectorType = connectorType;
    this.connectorEndType = connectorEndType;

  }

  /**
   * Creates a collection of {@link EaConnectorEnd} from the given element and its connectors.
   */
  public static Collection<EaConnectorEnd> createConnectorEnds(Element element) {
    List<EaConnectorEnd> connectorEnds = new ArrayList<>();
    for (Iterator<Connector> iterator = element.GetConnectors().iterator(); iterator.hasNext();) {
      Connector connector = iterator.next();
      ConnectorType connectorType = ConnectorType.findType(connector.GetType());
      ConnectorEndType connectorEndType;
      ConnectorEnd connectorEnd;
      ConnectorEnd oppositeConnectorEnd;
      int oppositeElementId;
      if (connector.GetClientID() == element.GetElementID()) {
        connectorEnd = connector.GetClientEnd();
        connectorEndType = ConnectorEndType.SOURCE;
        oppositeConnectorEnd = connector.GetSupplierEnd();
        oppositeElementId = connector.GetSupplierID();
      } else if (connector.GetSupplierID() == element.GetElementID()) {
        connectorEnd = connector.GetSupplierEnd();
        connectorEndType = ConnectorEndType.TARGET;
        oppositeConnectorEnd = connector.GetClientEnd();
        oppositeElementId = connector.GetClientID();
      } else {
        throw new IllegalArgumentException(EaModelUtils.toString(element) + " is not related to "
            + EaModelUtils.toString(connector));
      }
      connectorEnds.add(new EaConnectorEnd(connector, connectorType, connectorEnd, connectorEndType,
          element, oppositeConnectorEnd, oppositeElementId));
    }
    return new UnmodifiableList<>(connectorEnds);
  }

  public Connector getConnector() {
    return connector;
  }

  public ConnectorType getConnectorType() {
    return connectorType;
  }

  public ConnectorEnd getConnectorEnd() {
    return connectorEnd;
  }

  public ConnectorEndType getConnectorEndType() {
    return connectorEndType;
  }

  public Element getElement() {
    return element;
  }

  public int getOppositeElementId() {
    return oppositeElementId;
  }

  public ConnectorEnd getOppositeConnectorEnd() {
    return oppositeConnectorEnd;
  }

  /**
   * Gets a unique id for the connector end based on the EA GUID of the connector.
   */
  public String getConnectorEndUniqueId() {
    String suffix;
    if (getConnectorEndType().equals(ConnectorEndType.SOURCE)) {
      suffix = ID_SUFFIX_SOURCE;
    } else {
      suffix = ID_SUFFIX_TARGET;
    }
    return getConnector().GetConnectorGUID().concat(suffix);
  }

  /**
   * Gets a unique id for the opposite connector end based on the EA GUID of the connector.
   */
  public String getOppositeConnectorEndUniqueId() {
    String suffix;
    if (getConnectorEndType().equals(ConnectorEndType.SOURCE)) {
      suffix = ID_SUFFIX_TARGET;
    } else {
      suffix = ID_SUFFIX_SOURCE;
    }
    return getConnector().GetConnectorGUID().concat(suffix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(connector.GetConnectorGUID(), element.GetElementGUID(), connectorEndType);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    EaConnectorEnd other = (EaConnectorEnd) obj;
    return Objects.equals(connector.GetConnectorGUID(), other.connector.GetConnectorGUID())
        && Objects.equals(element.GetElementGUID(), other.element.GetElementGUID())
        && Objects.equals(connectorEndType, other.connectorEndType);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("element", EaModelUtils.toString(element))
        .append("connector", EaModelUtils.toString(connector))
        .append("connector end type", connectorEndType.toString()).toString();
  }

  /**
   * A connector end can be the source (client) or the target (supplier) of the connector.
   */
  public enum ConnectorEndType {
    SOURCE, TARGET
  }

}
