package dk.gov.data.modellingtools.export.conceptmodel.impl;

import dk.gov.data.modellingtools.config.FreemarkerTemplateConfiguration;
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

  public ConceptModelExporterImpl(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  public void exportConceptModel(String packageGuid, File folder, String format)
      throws ModellingToolsException {
    LOGGER.info("Start exporting concept model to format {}", format);
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
    dataForTemplate.put("conceptModel", getConceptModel());
    dataForTemplate.put("diagrams",
        new SimpleCollection(diagramDao.findAllByPackageGuid(getPackageGuid()),
            FreemarkerTemplateConfiguration.INSTANCE.getConfiguration().getObjectWrapper()));
    List<Concept> concepts = conceptDao.findAllByPackageGuid(getPackageGuid());
    // sort alphabetically on the Danish preferred term
    concepts.sort((c1, c2) -> c1.getPreferredTerms().get("da")
        .compareToIgnoreCase(c2.getPreferredTerms().get("da")));
    dataForTemplate.put("concepts", new SimpleCollection(concepts,
        FreemarkerTemplateConfiguration.INSTANCE.getConfiguration().getObjectWrapper()));
    return dataForTemplate;
  }

  @Override
  protected String getTemplateFileNamePrefix() {
    return "concept_model_";
  }

  @Override
  protected void prepareExport() throws ModellingToolsException {
    conceptModelDao = new ConceptModelDaoFda(getEaWrapper());
    conceptDao = new ConceptDaoFda(getEaWrapper());
    diagramDao = new DiagramDaoImpl(getEaWrapper());
  }

  @Override
  protected String getOutputFileName() throws ModellingToolsException {
    return FolderAndFileUtils.createFileNameWithoutSpaces(getConceptModel().getTitles().get("da"),
        getConceptModel().getVersion());
  }

  private ConceptModel getConceptModel() throws ModellingToolsException {
    if (conceptModel == null) {
      conceptModel = conceptModelDao.findByPackageGuid(getPackageGuid());
    }
    return conceptModel;
  }

}
