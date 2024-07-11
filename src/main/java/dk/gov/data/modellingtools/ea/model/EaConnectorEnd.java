package dk.gov.data.modellingtools.ea.model;

import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

  public static final String ID_PREFIX_SOURCE = "src";
  public static final String ID_PREFIX_TARGET = "dst";

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
    for (Connector connector : element.GetConnectors()) {
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
    String prefix;
    if (getConnectorEndType().equals(ConnectorEndType.SOURCE)) {
      prefix = ID_PREFIX_SOURCE;
    } else {
      prefix = ID_PREFIX_TARGET;
    }
    return createConnectorEndUniqueId(prefix);
  }

  /**
   * Gets a unique id for the opposite connector end based on the EA GUID of the connector.
   */
  public String getOppositeConnectorEndUniqueId() {
    String prefix;
    if (getConnectorEndType().equals(ConnectorEndType.SOURCE)) {
      prefix = ID_PREFIX_TARGET;
    } else {
      prefix = ID_PREFIX_SOURCE;
    }
    return createConnectorEndUniqueId(prefix);
  }

  /**
   * Create a "GUID-like" id, so starting and ending with a curly brace.
   */
  private String createConnectorEndUniqueId(String prefix) {
    String connectorGuid = getConnector().GetConnectorGUID();
    /*
     * The connector GUID starts with a curly brace. The next two characters are removed. Therefore
     * remove three characters in total. String are 0-based indexed in Java, so substring(3) is
     * needed.
     * 
     * For example: the connector GUID {0D467508-0484-4a26-B670-7A54ABB7F73D} is transformed to
     * {src467508-0484-4a26-B670-7A54ABB7F73D} and {dst467508-0484-4a26-B670-7A54ABB7F73D}.
     */
    return "{" + prefix + connectorGuid.substring(3);
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
