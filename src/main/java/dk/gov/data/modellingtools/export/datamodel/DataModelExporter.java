package dk.gov.data.modellingtools.export.datamodel;

import dk.gov.data.modellingtools.app.StrictnessMode;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Exports a data model.
 */
public interface DataModelExporter {

  void exportDataModel(String packageGuid, File folder, String format, Locale locale,
      StrictnessMode strictnessMode) throws ModellingToolsException;

  List<String> getSupportedFormats();

}
