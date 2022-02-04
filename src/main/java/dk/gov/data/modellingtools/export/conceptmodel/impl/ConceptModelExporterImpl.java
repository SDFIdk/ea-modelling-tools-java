package dk.gov.data.modellingtools.export.conceptmodel.impl;

import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.dao.ConceptModelDao;
import dk.gov.data.modellingtools.dao.DiagramDao;
import dk.gov.data.modellingtools.dao.impl.ConceptDaoFda;
import dk.gov.data.modellingtools.dao.impl.ConceptModelDaoFda;
import dk.gov.data.modellingtools.dao.impl.DiagramDaoImpl;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.AbstractExporter;
import dk.gov.data.modellingtools.export.conceptmodel.ConceptModelExporter;
import dk.gov.data.modellingtools.model.Concept;
import dk.gov.data.modellingtools.model.ConceptModel;
import dk.gov.data.modellingtools.utils.FolderAndFileUtils;
import freemarker.template.Configuration;
import freemarker.template.SimpleCollection;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exports a concept model.
 */
public class ConceptModelExporterImpl extends AbstractExporter implements ConceptModelExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConceptModelExporterImpl.class);

  private ConceptDao conceptDao;

  private ConceptModelDao conceptModelDao;

  private DiagramDao diagramDao;

  private ConceptModel conceptModel;

  public ConceptModelExporterImpl(EnterpriseArchitectWrapper eaWrapper,
      Configuration templateConfiguration) {
    super(eaWrapper, templateConfiguration);
  }

  @Override
  public void exportConceptModel(String packageGuid, File folder, String format)
      throws ModellingToolsException {
    LOGGER.info("Start exporting {} to format {}", conceptModel, format);
    export(packageGuid, folder, format);
  }

  @Override
  public List<String> getSupportedFormats() {
    return List.of("asciidoc", "rdf");
  }

  @Override
  protected Map<String, Object> prepareDataForTemplate() throws ModellingToolsException {
    // TODO add parameter profile to support other profiles than FDA
    Map<String, Object> dataForTemplate = new HashMap<>();
    dataForTemplate.put("conceptModel", conceptModel);
    dataForTemplate.put("diagrams", new SimpleCollection(diagramDao.findAll(getPackage()),
        templateConfiguration.getObjectWrapper()));
    List<Concept> concepts = conceptDao.findAll(getPackage());
    // sort alphabetically on the Danish preferred term
    concepts.sort((c1, c2) -> c1.getPreferredTerms().get("da")
        .compareToIgnoreCase(c2.getPreferredTerms().get("da")));
    dataForTemplate.put("concepts",
        new SimpleCollection(concepts, templateConfiguration.getObjectWrapper()));
    return dataForTemplate;
  }

  @Override
  protected String getTemplateFileNamePrefix() {
    return "concept_model_";
  }

  @Override
  protected void prepareExport() throws ModellingToolsException {
    conceptModelDao = new ConceptModelDaoFda();
    conceptModel = conceptModelDao.findByPackage(getPackage());
    conceptDao = new ConceptDaoFda();
    diagramDao = new DiagramDaoImpl();
  }

  @Override
  protected String getOutputFileName() {
    return FolderAndFileUtils.createFileNameWithoutSpaces(conceptModel.getTitles().get("da"),
        conceptModel.getVersion());
  }

}
