package dk.gov.data.modellingtools.export.conceptmodel;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;
import java.util.List;

/**
 * Exports a concept model that is modelling according to the FDA modelling rules.
 */
public interface ConceptModelExporter {

  void exportConceptModel(String packageGuid, File folder, String format)
      throws ModellingToolsException;

  List<String> getSupportedFormats();

}
