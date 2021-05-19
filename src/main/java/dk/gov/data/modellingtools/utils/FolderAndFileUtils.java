package dk.gov.data.modellingtools.utils;

import dk.gov.data.modellingtools.exception.DataModellingToolsException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderAndFileUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(FolderAndFileUtils.class);

  /**
   * Validate that the given folder is actually a folder, then create the folder.
   * 
   * @param folder folder to create
   */
  public static void validateAndCreateFolderIfNeeded(File folder)
      throws DataModellingToolsException {
    Validate.notNull(folder);
    if (folder.exists()) {
      Validate.isTrue(folder.isDirectory(), folder.getAbsolutePath() + " is not a directory");
    } else {
      boolean mkdirResult = folder.mkdir();
      if (!mkdirResult) {
        throw new DataModellingToolsException("Could not create folder " + folder);
      }
    }
  }

  /**
   * Delete and create a file in the file system.
   * 
   * @param file file to delete and create again
   */
  public static void deleteAndCreate(File file) throws DataModellingToolsException {
    try {
      boolean fileDeleted = Files.deleteIfExists(file.toPath());
      if (!fileDeleted) {
        LOGGER.debug("File " + file.getAbsolutePath() + " does not yet exist");
      }
      boolean fileCreated = file.createNewFile();
      LOGGER.debug("Result of file creation of " + file.getAbsolutePath() + ": " + fileCreated);
    } catch (IOException e) {
      throw new DataModellingToolsException(
          "Could not create file " + file.getAbsolutePath() + e.getMessage(), e);
    }
  }

}
