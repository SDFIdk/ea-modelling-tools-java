package dk.gov.data.modellingtools.scriptmanagement;

import dk.gov.data.modellingtools.exception.DataModellingToolsException;
import java.io.File;

public interface ScriptManager {

  /**
   * Exports the scripts in the given script group(s) to the given folder.
   * 
   * @param scriptGroupNameOrRegex name of the script group, or regex defining one or more script
   *        groups. Single quotes will be escaped. <a href=
   *        "https://docs.microsoft.com/en-us/previous-versions/office/developer/office2000/aa140015(v=office.10)">Supported
   *        wildcards</a>:
   *        <table>
   *        <caption>Wildcards</caption> <thead>
   *        <tr>
   *        <th>Wildcard Character</th>
   *        <th>Description</th>
   *        </tr>
   *        </thead> <tbody>
   *        <tr>
   *        <td>* (asterisk)</td>
   *        <td>Matches any number of characters and can be used anywhere in the pattern
   *        string.</td>
   *        </tr>
   *        <tr>
   *        <td>? (question mark)</td>
   *        <td>Matches any single character and can be used anywhere in the pattern string.</td>
   *        </tr>
   *        <tr>
   *        <td># (number sign)</td>
   *        <td>Matches any single digit and can be used anywhere in the pattern string.</td>
   *        </tr>
   *        <tr>
   *        <td>[] (square brackets)</td>
   *        <td>Matches any single character within the list that is enclosed within brackets, and
   *        can be used anywhere in the pattern string.</td>
   *        </tr>
   *        <tr>
   *        <td>! (exclamation point)</td>
   *        <td>Matches any single character not in the list that is enclosed within the square
   *        brackets.</td>
   *        </tr>
   *        <tr>
   *        <td>- (hyphen)</td>
   *        <td>Matches any one of a range of characters that is enclosed within the square
   *        brackets.</td>
   *        </tr>
   *        </tbody>
   *        </table>
   * @param folder The scripts are created in a folder with name "scriptGroupName", that will be a
   *        subdirectory of this folder.
   */
  void exportScripts(String scriptGroupNameOrRegex, File folder) throws DataModellingToolsException;

}
