package dk.gov.data.modellingtools.ea;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.util.Collection;
import org.apache.commons.collections4.MultiValuedMap;
import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.Element;
import org.sparx.Package;

/**
 * Interface to hide EA specific functionality behind. One implementation is used in production,
 * other implementations can be used for testing purposes.
 */
public interface EnterpriseArchitectWrapper {

  void writeToScriptWindow(String message) throws ModellingToolsException;

  /**
   * Returns an XML formatted string value of the resulting record set. It has the following
   * structure (presented as XPath): /EADATA/Dataset_0/Data/Row and uses UTF-8 encoding (starts with
   * <?xml version="1.0"?>, thus UTF-8 encoding). The column names retrieved by the input (without
   * "alias.") are used as names for the XML elements contained in Row.
   */
  String sqlQuery(String query) throws ModellingToolsException;

  Package getPackageByGuid(String packageGuid) throws ModellingToolsException;

  Element getElementById(int id);

  Element getElementByGuid(String elementGuid);

  Attribute getAttributeByGuid(String attributeGuid);

  Connector getConnectorByGuid(String connectorGuid);

  Collection<String> retrievePackageFqStereotypes(Package umlPackage)
      throws ModellingToolsException;

  /**
   * Returns a multi-valued map containing the GUIDs of the element in the given package as keys,
   * and their fully-qualified names as values.
   */
  MultiValuedMap<String, String> retrieveElementFqStereotypes(Package umlPackage)
      throws ModellingToolsException;

  /**
   * Returns a multi-valued map containing the GUIDs of the attribute in the given package as keys,
   * and their fully-qualified names as values.
   */
  MultiValuedMap<String, String> retrieveAttributeFqStereotypes(Package umlPackage)
      throws ModellingToolsException;

  MultiValuedMap<String, String> retrieveConnectorEndFqStereotypes() throws ModellingToolsException;

  /**
   * If a connector is controlled in the same package as a certain element, this means that both the
   * element and the connector can be changed when the package the element belongs to is checked
   * out. See also [Add Connectors To Locked
   * Elements](https://www.sparxsystems.com/search/sphider/search.php?query=%22Add%20Connectors%20To%20Locked%20Elements%22&type=phrase&category=User+Guide+Latest&tab=5&search=1)
   *
   * @return whether the given connector is an association (including aggregations and compositions)
   *         controlled in the same package as in the given element
   */
  boolean isConnectorAssociationAndControlledInSamePackageAsElement(Connector connector,
      Element element);

  boolean transferProject(String sourceFilePath, String targetFilePath)
      throws ModellingToolsException;

  /**
   * Returns the type of repository (based on the underlying database).
   */
  RepositoryType getRepositoryType();

  /**
   * Returns the major version of Enterprise Architect. E.g. 15 or 16.
   */
  int getMajorVersion() throws ModellingToolsException;

  /**
   * Terminates whatever needs to be terminated.
   */
  void terminate();

  /**
   * Returns the version of the MDG that is used by the EA repository.
   */
  String getMdgVersion(String mdgId);

  /**
   * Reloads the given package (the user interface is updated).
   */
  void reloadPackage(Package umlPackage);

}
