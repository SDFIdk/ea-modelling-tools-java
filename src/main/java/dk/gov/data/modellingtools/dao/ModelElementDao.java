package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.ModelElement;
import java.util.Collection;

/**
 * Data access object for {@link ModelElement}s.
 */
public interface ModelElementDao {

  String NO_STEREOTYPE = "NoStereotype";

  Collection<ModelElement> findAllByPackageGuid(String packageGuid) throws ModellingToolsException;

}
