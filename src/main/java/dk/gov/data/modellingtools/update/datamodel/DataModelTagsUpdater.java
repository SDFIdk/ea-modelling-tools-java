package dk.gov.data.modellingtools.update.datamodel;

import dk.gov.data.modellingtools.app.TagUpdateMode;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;

/**
 * Update the tags of a data model with the information from a file.
 */
public interface DataModelTagsUpdater {

  void updateDataModel(String packageGuid, File file, TagUpdateMode tagUpdateMode)
      throws ModellingToolsException;

}
