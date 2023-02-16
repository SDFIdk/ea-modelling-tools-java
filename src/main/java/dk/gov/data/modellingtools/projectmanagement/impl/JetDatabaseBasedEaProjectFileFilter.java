package dk.gov.data.modellingtools.projectmanagement.impl;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

class JetDatabaseBasedEaProjectFileFilter implements IOFileFilter {
  private boolean acceptExtension(final String name) {
    return FilenameUtils.isExtension(name, "eap", "eapx", "EAP", "EAPX");
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
