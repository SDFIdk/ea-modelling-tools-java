package dk.gov.data.modellingtools.projectmanagement.impl;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.projectmanagement.ProjectTransferer;
import dk.gov.data.modellingtools.utils.FileFormatUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.AccumulatorPathVisitor;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link ProjectTransferer}.
 */
public class ProjectTransfererImpl implements ProjectTransferer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectTransfererImpl.class);

  private EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public ProjectTransfererImpl(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public void transferProjects(File inputFolder, String inputFormat, String outputFormat)
      throws ModellingToolsException {
    Validate.notNull(inputFolder, "The input folder must not be null");
    Validate.isTrue(inputFolder.exists(), "The input folder must exist",
        inputFolder.getAbsolutePath());
    Validate.isTrue(inputFolder.isDirectory(), "The input folder must be a folder",
        inputFolder.getAbsolutePath());
    Validate.notNull(inputFormat, "An input format must be given");
    Validate.notNull(outputFormat, "An output format must be given");

    int eaMajorVersion = eaWrapper.getMajorVersion();
    if (eaMajorVersion == 16 && (inputFormat.toLowerCase().startsWith("eap")
        || outputFormat.toLowerCase().startsWith("eap"))) {
      LOGGER.warn(
          "If you are running a 64 bit EA, you cannot transfer from or to eap(x) files. The created files will contain empty tables.");
    } else if (eaMajorVersion <= 15 && (inputFormat.toLowerCase().startsWith("qea")
        || outputFormat.toLowerCase().startsWith("qea"))) {
      throw new IllegalArgumentException(
          "You cannot use EA 15 to transfer from or to SQLite based projects (qea(x))");
    }

    AccumulatorPathVisitor visitor = AccumulatorPathVisitor
        .withLongCounters(createFileFilter(inputFormat), DirectoryFileFilter.DIRECTORY);
    try {
      Files.walkFileTree(Paths.get(inputFolder.getAbsolutePath()), visitor);
      List<Path> fileList = visitor.getFileList();
      if (fileList.size() > 0) {
        for (int i = 0; i < fileList.size(); i++) {
          Path path = fileList.get(i);
          File sourceProjectFile = path.toFile();
          File targetProjectFile = new File(sourceProjectFile.getParent(),
              FilenameUtils.getBaseName(sourceProjectFile.getName())
                  + FileFormatUtils.getFileFormatExtension(outputFormat));
          if (targetProjectFile.exists()) {
            LOGGER.warn("Not transferring {} as {} exists already",
                sourceProjectFile.getAbsolutePath(), targetProjectFile.getAbsolutePath());
          } else {
            LOGGER.info("Transferring project " + sourceProjectFile.getAbsolutePath() + " to "
                + targetProjectFile.getAbsolutePath());
            eaWrapper.transferProject(sourceProjectFile.getAbsolutePath(),
                targetProjectFile.getAbsolutePath());
          }
        }
      }
    } catch (IOException e) {
      throw new ModellingToolsException(
          "An I/O error occurred while checking the input folder for relevant files", e);
    }

  }

  private IOFileFilter createFileFilter(String format) {
    final IOFileFilter fileFilter;
    switch (format.toLowerCase()) {
      case "eap":
      case "eapx":
        fileFilter = new JetDatabaseBasedEaProjectFileFilter();
        break;
      case "feap":
        fileFilter = new FirebirdBasedEaProjectFileFilter();
        break;
      case "qea":
      case "qeax":
        fileFilter = new SqliteBaseEaProjectFileFilter();
        break;
      default:
        throw new IllegalArgumentException("Format " + format + " is not supported");
    }
    return fileFilter;
  }


}
