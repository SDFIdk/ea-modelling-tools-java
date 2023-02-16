package dk.gov.data.modellingtools.projectmanagement;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;

/**
 * Interface for a project transferer.
 */
public interface ProjectTransferer {

  /**
   * Transfers projects in a folder (including subfolders) from one format to another format.
   * 
   * <p>See also method ProjectTransfer on the Project class in the EA automation reference for more
   * information.</p>
   */
  void transferProjects(File folder, String inputFormat, String outputFormat)
      throws ModellingToolsException;

}
