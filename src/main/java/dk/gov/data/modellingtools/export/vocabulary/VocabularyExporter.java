package dk.gov.data.modellingtools.export.vocabulary;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Exports a vocabulary extracted from a data model.
 */
public interface VocabularyExporter {

  void exportVocabulary(String packageGuid, File folder, String format, String language,
      boolean hasHeader, boolean hasMetadata, URL metadataUrl) throws ModellingToolsException;

  List<String> getSupportedFormats();

}
