package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.SemanticModelElement;
import java.util.List;

/**
 * Data access object (DAO) for {@link SemanticModelElement}s.
 */
public interface SemanticModelElementDao {

  List<SemanticModelElement> findAllByPackageGuid(String packageGuid)
      throws ModellingToolsException;

}
