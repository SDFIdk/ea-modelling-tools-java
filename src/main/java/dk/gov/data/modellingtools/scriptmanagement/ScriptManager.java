package dk.gov.data.modellingtools.scriptmanagement;

import dk.gov.data.modellingtools.exception.DataModellingToolsException;
import freemarker.template.Configuration;
import java.io.File;

/**
 * Interface for the management of scripts.
 */
public interface ScriptManager {

  /**
   * Exports the scripts in the given script group(s) to the given folder.
   * 
   * @param scriptGroupNameOrRegex name of the script group, or regex defining one or more script
   *        groups. Single quotes will be escaped. The wildcards that are supported are the ones
   *        from the Microsoft Jet SQL engine (on which an Enterprise Architect project file is
   *        based), see also
   *        https://docs.microsoft.com/en-us/previous-versions/office/developer/office2000/aa140015(v=office.10)
   *        and https://www.w3schools.com/SQL/sql_like.asp.
   * @param folder The scripts are created in a folder with name "scriptGroupName", that will be a
   *        subdirectory of this folder.
   */
  void exportScripts(String scriptGroupNameOrRegex, File folder, boolean createDocumentation,
      Configuration templateConfiguration) throws DataModellingToolsException;

}
