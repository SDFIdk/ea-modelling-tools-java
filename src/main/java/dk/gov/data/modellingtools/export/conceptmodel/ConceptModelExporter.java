package dk.gov.data.modellingtools.export.conceptmodel;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import freemarker.template.Configuration;
import java.io.File;
import java.util.List;

public interface ConceptModelExporter {

  void exportConceptModel(String packageGuid, File folder, String format,
      Configuration templateConfiguration) throws ModellingToolsException;

  List<String> getSupportedFormats();

}
