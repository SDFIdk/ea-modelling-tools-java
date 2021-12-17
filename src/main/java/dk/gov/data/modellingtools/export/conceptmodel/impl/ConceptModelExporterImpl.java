package dk.gov.data.modellingtools.export.conceptmodel.impl;

import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.dao.ConceptModelDao;
import dk.gov.data.modellingtools.dao.DiagramDao;
import dk.gov.data.modellingtools.dao.impl.ConceptDaoFda;
import dk.gov.data.modellingtools.dao.impl.ConceptModelDaoFda;
import dk.gov.data.modellingtools.dao.impl.DiagramDaoImpl;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.conceptmodel.ConceptModelExporter;
import dk.gov.data.modellingtools.utils.FileFormatUtils;
import dk.gov.data.modellingtools.utils.FolderAndFileUtils;
import freemarker.template.Configuration;
import freemarker.template.SimpleCollection;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.asciidoctor.Asciidoctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;

/**
 * Exports a concept model.
 */
public class ConceptModelExporterImpl implements ConceptModelExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConceptModelExporterImpl.class);

  private EnterpriseArchitectWrapper eaWrapper;

  private ConceptDao conceptDao;

  private ConceptModelDao conceptModelDao;

  private DiagramDao diagramDao;

  public ConceptModelExporterImpl(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public void exportConceptModel(String packageGuid, File folder, String format,
      Configuration templateConfiguration) throws ModellingToolsException {
    LOGGER.info("Start exporting concept model to format " + format);

    // process input parameters
    String outputFileExtension = FileFormatUtils.getFileFormatExtension(format);
    String templateFileName =
        "concept_model_" + format + FileFormatUtils.getTemplateExtension(format);
    Package umlPackage = eaWrapper.getPackageByGuid(packageGuid);
    Validate.notNull(umlPackage, "No package found for GUID " + packageGuid);

    // prepare folders and files
    FolderAndFileUtils.validateAndCreateFolderIfNeeded(folder);
    File outputFile = new File(folder, umlPackage.GetName() + outputFileExtension);
    FolderAndFileUtils.deleteAndCreate(outputFile);

    Map<String, Object> dataForTemplate = prepareDataForTemplate(umlPackage, templateConfiguration);

    try (BufferedWriter writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"))) {

      // retrieve and populate template
      Template template = templateConfiguration.getTemplate(templateFileName);
      template.process(dataForTemplate, writer);

      if ("asciidoc".equals(format)) {
        // convert Asciidoc file to HTML
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        HashMap<String, Object> options = new HashMap<>();
        // https://docs.asciidoctor.org/asciidoctor/latest/safe-modes/
        // set to 1, so the default CSS stylesheets are used
        options.put("safe", 1);
        asciidoctor.convertFile(outputFile, options);
      }
      LOGGER.info("Finished exporting concept model to " + folder.getAbsolutePath());
    } catch (IOException e) {
      throw new ModellingToolsException(
          "Could not write content to " + outputFile.getAbsolutePath() + ": " + e.getMessage(), e);
    } catch (TemplateException e) {
      throw new ModellingToolsException(
          "Could not process template " + templateFileName + ": " + e.getMessage(), e);
    }
  }

  @Override
  public List<String> getSupportedFormats() {
    return List.of("asciidoc", "rdf");
  }

  private Map<String, Object> prepareDataForTemplate(Package umlPackage,
      Configuration templateConfiguration) throws ModellingToolsException {
    // TODO add parameter profile to support other profiles then FDA
    conceptDao = new ConceptDaoFda();
    conceptModelDao = new ConceptModelDaoFda();
    diagramDao = new DiagramDaoImpl();
    Map<String, Object> dataForTemplate = new HashMap<>();
    dataForTemplate.put("conceptModel", conceptModelDao.findByPackage(umlPackage));
    dataForTemplate.put("diagrams", new SimpleCollection(diagramDao.findAll(umlPackage),
        templateConfiguration.getObjectWrapper()));
    dataForTemplate.put("concepts", new SimpleCollection(conceptDao.findAll(umlPackage),
        templateConfiguration.getObjectWrapper()));
    return dataForTemplate;
  }

}
