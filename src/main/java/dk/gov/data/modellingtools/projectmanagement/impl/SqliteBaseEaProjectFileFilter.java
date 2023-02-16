package dk.gov.data.modellingtools.projectmanagement.impl;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

class SqliteBaseEaProjectFileFilter implements IOFileFilter {

  private boolean acceptExtension(final String name) {
    return FilenameUtils.isExtension(name, "qea", "qeax", "QEA", "QEAX");
  }

  @Override
  public boolean accept(File dir, String name) {
    return acceptExtension(name);
  }

  @Override
  public boolean accept(File file) {
    return acceptExtension(file.getName());
  }

}
