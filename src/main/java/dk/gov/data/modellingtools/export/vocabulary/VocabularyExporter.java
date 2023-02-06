package dk.gov.data.modellingtools.export.vocabulary;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Exports a vocabulary extracted from a data model.
 */
public interface VocabularyExporter {

  void exportVocabulary(String packageGuid, File folder, String format, Locale locale,
      boolean hasHeader, boolean hasMetadata, URL metadataUrl) throws ModellingToolsException;

  List<String> getSupportedFormats();

}
