package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.ConnectorEnd;

/**
 * Abstract class with additional methods to the ones in {@link ConceptDao}, with EA-specific
 * parameters. This DAO can be used for retrieving concepts from logical data models (thus where not
 * each concept is modelled as a separate UML class, but the concepts are retrieved from classes,
 * attributes, association ends, etc.).
 */
public abstract class EaConceptDaoForLogicalDataModel extends AbstractEaConceptDao {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(EaConceptDaoForLogicalDataModel.class);

  public Concept findByAttribute(Attribute attribute) throws ModellingToolsException {
    LOGGER.debug("Finding concept for {}", EaModelUtils.toString(attribute));
    return findByObject(attribute);
  }

  public Concept findByConnectorEnd(ConnectorEnd connectorEnd) throws ModellingToolsException {
    LOGGER.debug("Finding concept for {}", EaModelUtils.toString(connectorEnd));
    return findByObject(connectorEnd);
  }


}
