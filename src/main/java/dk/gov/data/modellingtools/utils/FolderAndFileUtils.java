package dk.gov.data.modellingtools.utils;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for handling of files and directories.
 */
public class FolderAndFileUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(FolderAndFileUtils.class);

  /**
   * Validate that the given folder is actually a folder, then create the folder.
   *
   * @param folder folder to create
   */
  public static void validateAndCreateFolderIfNeeded(File folder) throws ModellingToolsException {
    Validate.notNull(folder);
    if (folder.exists()) {
      Validate.isTrue(folder.isDirectory(), folder.getAbsolutePath() + " is not a directory");
    } else {
      boolean mkdirResult = folder.mkdir();
      if (!mkdirResult) {
        throw new ModellingToolsException("Could not create folder " + folder);
      }
    }
  }

  /**
   * Delete and create a file in the file system.
   *
   * @param file file to delete and create again
   */
  public static void deleteAndCreate(File file) throws ModellingToolsException {
    try {
      boolean fileDeleted = Files.deleteIfExists(file.toPath());
      if (!fileDeleted) {
        LOGGER.debug("File {} does not yet exist", file.getAbsolutePath());
      }
      boolean fileCreated = file.createNewFile();
      LOGGER.debug("Result of file creation of {}: {}", file.getAbsolutePath(), fileCreated);
    } catch (IOException e) {
      throw new ModellingToolsException(
          "Could not create file " + file.getAbsolutePath() + ": " + e.getMessage(), e);
    }
  }

  /**
   * Replaces any spaces in the given names with underscores, and adds a version to the given name.
   */
  public static String createFileNameWithoutSpaces(String name, String version) {
    return StringUtils.replaceChars(name, ' ', '_') + "_v" + version;
  }

}
