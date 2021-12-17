package dk.gov.data.modellingtools.dao;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.ScriptGroup;
import java.io.File;
import java.util.List;

public interface ScriptGroupDao {

  /**
   * Find all script groups, the scripts in the script groups inclusive.
   * 
   * @see ScriptGroup#getScripts()
   */
  List<ScriptGroup> findAllIncludingScripts(String scriptGroupNameOrRegex)
      throws ModellingToolsException;

  /**
   * Save all script groups, the scripts in the scripts groups inclusive, as EA reference data.
   */
  void saveAllIncludingScriptsAsEaReferenceData(String scriptGroupNameOrRegex, File referenceData)
      throws ModellingToolsException;

}
