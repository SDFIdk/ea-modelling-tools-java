package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.SemanticModelElement;
import java.util.List;
import org.sparx.Package;

/**
 * Data access object (DAO) from {@link SemanticModelElement}s.
 */
public interface SemanticModelElementDao {

  List<SemanticModelElement> findAll(Package umlPackage) throws ModellingToolsException;

}
