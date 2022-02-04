package dk.gov.data.modellingtools.export;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.utils.FileFormatUtils;
import dk.gov.data.modellingtools.utils.FolderAndFileUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Package;

/**
 * Abstract superclass for functionality that exports a model.
 */
public abstract class AbstractExporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExporter.class);
  protected EnterpriseArchitectWrapper eaWrapper;
  private String outputFileExtension;
  private String templateFileName;
  private Package umlPackage;
  private File outputFile;
  private File outputFolder;
  private String outputFormat;
  protected Configuration templateConfiguration;
  private String packageGuid;

  /**
   * Constructor.
   */
  public AbstractExporter(EnterpriseArchitectWrapper eaWrapper,
      Configuration templateConfiguration) {
    super();
    this.eaWrapper = eaWrapper;
    this.templateConfiguration = templateConfiguration;
  }

  /**
   * Builds the data that will be merged with the template. See also
   * https://freemarker.apache.org/docs/pgui_quickstart_createdatamodel.html
   */
  protected abstract Map<String, Object> prepareDataForTemplate() throws ModellingToolsException;

  protected final void export(String packageGuid, File outputFolder, String outputFormat)
      throws ModellingToolsException {
    processInputParameters(packageGuid, outputFolder, outputFormat);
    prepareExport();
    prepareFoldersAndFiles();
    writeFiles(prepareDataForTemplate());
  }

  private void processInputParameters(String packageGuid, File outputFolder, String outputFormat)
      throws ModellingToolsException {
    this.packageGuid = packageGuid;
    this.outputFolder = outputFolder;
    this.outputFormat = outputFormat;
    this.outputFileExtension = FileFormatUtils.getFileFormatExtension(this.outputFormat);
    this.templateFileName = getTemplateFileNamePrefix() + this.outputFormat
        + FileFormatUtils.getTemplateExtension(this.outputFormat);
  }

  /**
   * Sets up any DAOs needed, retrieves the model to be exported, .... Is called before
   * {@link #prepareFoldersAndFiles()}.
   */
  protected abstract void prepareExport() throws ModellingToolsException;

  protected Package getPackage() throws ModellingToolsException {
    if (this.umlPackage == null) {
      this.umlPackage = eaWrapper.getPackageByGuid(packageGuid);
      Validate.notNull(this.umlPackage, "No package found for GUID " + packageGuid);
    }
    return this.umlPackage;
  }

  protected abstract String getTemplateFileNamePrefix();

  private void prepareFoldersAndFiles() throws ModellingToolsException {
    FolderAndFileUtils.validateAndCreateFolderIfNeeded(outputFolder);
    outputFile = new File(outputFolder, getOutputFileName() + outputFileExtension);
    FolderAndFileUtils.deleteAndCreate(outputFile);
  }

  protected abstract String getOutputFileName();

  private void writeFiles(Map<String, Object> dataForTemplate) throws ModellingToolsException {
    try (BufferedWriter writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"))) {

      // retrieve and populate template
      LOGGER.debug("Using template {}", templateFileName);
      Template template = templateConfiguration.getTemplate(templateFileName);
      template.process(dataForTemplate, writer);

      if ("asciidoc".equals(outputFormat)) {
        // convert Asciidoc file to HTML
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        // https://docs.asciidoctor.org/asciidoctor/latest/safe-modes/
        // set to SAFE (1), so the default CSS stylesheets are used
        Options options2 = Options.builder().safe(SafeMode.SAFE).build();
        asciidoctor.convertFile(outputFile, options2);
      }
      LOGGER.info("Finished exporting to {}", outputFile.getAbsolutePath());
    } catch (IOException e) {
      throw new ModellingToolsException(
          "Could not write content to " + outputFile.getAbsolutePath() + ": " + e.getMessage(), e);
    } catch (TemplateException e) {
      throw new ModellingToolsException(
          "Could not process template " + templateFileName + ": " + e.getMessage(), e);
    }
  }

  public EnterpriseArchitectWrapper getEaWrapper() {
    return eaWrapper;
  }



}
