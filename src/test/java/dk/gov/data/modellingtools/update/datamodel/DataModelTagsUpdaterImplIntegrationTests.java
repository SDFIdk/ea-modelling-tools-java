package dk.gov.data.modellingtools.update.datamodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dk.gov.data.modellingtools.AbstractEaTest;
import dk.gov.data.modellingtools.app.TagUpdateMode;
import dk.gov.data.modellingtools.constants.BasicData2Constants;
import dk.gov.data.modellingtools.ea.impl.EnterpriseArchitectWrapperImpl;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.export.datamodel.impl.DataModelExporterImplIntegrationTests;
import dk.gov.data.modellingtools.update.datamodel.impl.DataModelTagsUpdaterImpl;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;

public class DataModelTagsUpdaterImplIntegrationTests extends AbstractEaTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DataModelTagsUpdaterImplIntegrationTests.class);

  private static File folderForTest;

  private Repository repository;

  private DataModelTagsUpdater dataModelTagsUpdater;


  private Package viewPackage;

  /**
   * Creates a test directory in the temporary directory.
   */
  @BeforeAll
  public static void createTestDirectory() {
    folderForTest = new File(FileUtils.getTempDirectory(),
        DataModelTagsUpdaterImplIntegrationTests.class.getSimpleName());
    LOGGER.debug("Testing in {}", folderForTest);
    boolean mkdir = folderForTest.mkdir();
    LOGGER.debug("{} created: {}", folderForTest.getAbsolutePath(), mkdir);
  }

  @BeforeEach
  public void cleanTestDirectoryANdCreateTestRepo() throws IOException {
    FileUtils.cleanDirectory(folderForTest);
    repository = createRepository(folderForTest);

    /* create a view (https://sparxsystems.com/eahelp/manageviews.html) to add packages to */
    /*
     * 0 indicates that this Package is a model (that is, it has no parent), see also
     * https://sparxsystems.com/eahelp/package_2.html
     */
    Package modelPackage = repository.GetModels().GetAt((short) 0);
    viewPackage = modelPackage.GetPackages().AddNew("View", null);
    viewPackage.Update();
    modelPackage.GetPackages().Refresh();
  }

  @AfterEach
  public void closeAndDeleteRepo() {
    closeAndDeleteRepository(repository);
  }

  @AfterAll
  public static void deleteTestDirectory() {
    FileUtils.deleteQuietly(folderForTest);
  }

  @Test
  public void testUpdateProfileTags() throws ModellingToolsException {
    assumeMdgWithVersionInstalled(repository, BasicData2Constants.MDG_ID, "2.2");

    URL dataModelUrl =
        DataModelExporterImplIntegrationTests.class.getResource("/datamodel/MinModel_GDv22.xmi");
    String importedPackageGuid = importXmiInPackage(dataModelUrl, repository, viewPackage);
    dataModelTagsUpdater =
        new DataModelTagsUpdaterImpl(new EnterpriseArchitectWrapperImpl(repository));
    File inputFile = FileUtils.toFile(DataModelTagsUpdaterImplIntegrationTests.class
        .getResource("/update/datamodel/MinModel_GDv22_v1.0.0-update-profiletags.csv"));
    dataModelTagsUpdater.updateDataModel(importedPackageGuid, inputFile, TagUpdateMode.UPDATE_ONLY);

    LOGGER.info("Update data model, now test whether it has been updated as expected.");

    Element dkObjekttype1 = repository.GetElementByGuid("{0D440DBC-36E8-42bb-97D2-4256F71BABE4}");
    assertNotNull(dkObjekttype1);
    assertEquals("objekttype1 UPDATED", TaggedValueUtils.getTaggedValueValue(dkObjekttype1,
        "Grunddata2::ModelElement::prefLabel (da)"));
    assertEquals("definition objekttype1 på engelsk UPDATED", TaggedValueUtils
        .getTaggedValueValue(dkObjekttype1, "Grunddata2::ModelElement::definition (en)"));
    assertEquals("applikationsnote objekttype1 på dansk UPDATED", TaggedValueUtils
        .getTaggedValueValue(dkObjekttype1, "Grunddata2::ModelElement::applicationNote (da)"));
    assertEquals("juridisk kilde objekttype1 UPDATED", TaggedValueUtils
        .getTaggedValueValue(dkObjekttype1, "Grunddata2::ModelElement::legalSource"));

    Attribute geometri = repository.GetAttributeByGuid("{D913CDA7-D596-48db-838F-9789E0E3E7B0}");
    assertEquals("EPSG:25833",
        TaggedValueUtils.getTaggedValueValue(geometri, "Grunddata2::DKEgenskab::CRS"));
    assertEquals("True",
        TaggedValueUtils.getTaggedValueValue(geometri, "Grunddata2::DKEgenskab::Z-used"));

    Attribute kode = repository.GetAttributeByGuid("{03CDA8A0-8F47-4b20-8C1C-603D967D6078}");
    assertEquals("kommentar kode på dansk UPDATED",
        TaggedValueUtils.getTaggedValueValue(kode, "Grunddata2::ModelElement::comment (da)"));
    assertEquals("kilde kode UPDATED",
        TaggedValueUtils.getTaggedValueValue(kode, "Grunddata2::ModelElement::source"));

    ConnectorEnd objekttype2rolle = EaModelUtils.findConnectorEnd(
        EaModelUtils.getAllAssociationsOfPackageAndSubpackages(
            repository.GetPackageByGuid(importedPackageGuid)),
        "{dst467508-0484-4a26-B670-7A54ABB7F73D}");
    assertEquals("definition objekttype2rolle på dansk UPDATED", TaggedValueUtils
        .getTaggedValueValue(objekttype2rolle, "Grunddata2::ModelElement::definition (da)"));
    assertEquals("applikationsnote objekttype2rolle på dansk UPDATED", TaggedValueUtils
        .getTaggedValueValue(objekttype2rolle, "Grunddata2::ModelElement::applicationNote (da)"));
    assertEquals("applikationsnote objekttype2rolle på engelsk UPDATED", TaggedValueUtils
        .getTaggedValueValue(objekttype2rolle, "Grunddata2::ModelElement::applicationNote (en)"));

    Element dkObjekttype2 = repository.GetElementByGuid("{FB7F8A23-ACE6-4bee-AC0A-0C334F53EF85}");
    assertEquals("bitemporalitet", TaggedValueUtils.getTaggedValueValue(dkObjekttype2,
        "Grunddata2::DKObjekttype::historikmodel"));

    Attribute vaerdi1 = repository.GetAttributeByGuid("{7318E431-AB6C-46ce-B8C1-DFEBB35A90EE}");
    assertEquals("kommentar værdi1 på engelsk UPDATED",
        TaggedValueUtils.getTaggedValueValue(vaerdi1, "Grunddata2::ModelElement::comment (en)"));

    Attribute vaerdi2 = repository.GetAttributeByGuid("{1C3ED97C-3A76-4a2c-8D51-CE28D7131DD4}");
    assertEquals("value2 UPDATED",
        TaggedValueUtils.getTaggedValueValue(vaerdi2, "Grunddata2::ModelElement::prefLabel (en)"));
    assertEquals("eksempel værdi2 på dansk UPDATED",
        TaggedValueUtils.getTaggedValueValue(vaerdi2, "Grunddata2::ModelElement::example (da)"));

    Element dkKodeliste1 = repository.GetElementByGuid("{91A4860B-5771-4091-9337-7DCBF2DD3FAC}");
    assertEquals("https://UPDATED.uri.kodeliste1", TaggedValueUtils
        .getTaggedValueValue(dkKodeliste1, "Grunddata2::DKKodeliste::vokabularium"));

  }

  @Test
  public void testUpdateCustom() throws ModellingToolsException {
    URL dataModelUrl =
        DataModelExporterImplIntegrationTests.class.getResource("/datamodel/MinModel_GDv22.xmi");
    String importedPackageGuid = importXmiInPackage(dataModelUrl, repository, viewPackage);
    dataModelTagsUpdater =
        new DataModelTagsUpdaterImpl(new EnterpriseArchitectWrapperImpl(repository));
    File inputFile = FileUtils.toFile(DataModelTagsUpdaterImplIntegrationTests.class
        .getResource("/update/datamodel/MinModel_GDv22_v1.0.0-update-customtags.csv"));
    dataModelTagsUpdater.updateDataModel(importedPackageGuid, inputFile,
        TagUpdateMode.UPDATE_ADD_DELETE);

    LOGGER.info("Update data model, now test whether it has been updated as expected.");

    Element dkObjekttype1 = repository.GetElementByGuid("{0D440DBC-36E8-42bb-97D2-4256F71BABE4}");
    assertNotNull(dkObjekttype1);
    String taggedValueNameTestTag = "testtag";
    assertEquals("test value DKObjekttype1",
        TaggedValueUtils.getTaggedValueValue(dkObjekttype1, taggedValueNameTestTag));

    Attribute geometri = repository.GetAttributeByGuid("{D913CDA7-D596-48db-838F-9789E0E3E7B0}");
    assertEquals("test value geometri",
        TaggedValueUtils.getTaggedValueValue(geometri, taggedValueNameTestTag));

    Attribute kode = repository.GetAttributeByGuid("{03CDA8A0-8F47-4b20-8C1C-603D967D6078}");
    assertEquals("", TaggedValueUtils.getTaggedValueValue(kode, taggedValueNameTestTag));
    assertNotNull(kode.GetTaggedValues().GetByName("CRS"),
        "Already existing CRS tag must not be deleted");
    assertNotNull(kode.GetTaggedValues().GetByName("Z-used"),
        "Already existing Z-used tag must not be deleted");


    ConnectorEnd objekttype2rolle = EaModelUtils.findConnectorEnd(
        EaModelUtils.getAllAssociationsOfPackageAndSubpackages(
            repository.GetPackageByGuid(importedPackageGuid)),
        "{dst467508-0484-4a26-B670-7A54ABB7F73D}");
    assertEquals("test value dkObjekttype2",
        TaggedValueUtils.getTaggedValueValue(objekttype2rolle, taggedValueNameTestTag));

    ConnectorEnd unnamedRole = EaModelUtils.findConnectorEnd(
        EaModelUtils.getAllAssociationsOfPackageAndSubpackages(
            repository.GetPackageByGuid(importedPackageGuid)),
        "{src467508-0484-4a26-B670-7A54ABB7F73D}");
    assertEquals("", TaggedValueUtils.getTaggedValueValue(unnamedRole, taggedValueNameTestTag));

    Element dkObjekttype2 = repository.GetElementByGuid("{FB7F8A23-ACE6-4bee-AC0A-0C334F53EF85}");
    assertEquals("", TaggedValueUtils.getTaggedValueValue(dkObjekttype2, taggedValueNameTestTag));

    Attribute vaerdi1 = repository.GetAttributeByGuid("{7318E431-AB6C-46ce-B8C1-DFEBB35A90EE}");
    assertEquals("test value værdi1",
        TaggedValueUtils.getTaggedValueValue(vaerdi1, taggedValueNameTestTag));

    Attribute vaerdi2 = repository.GetAttributeByGuid("{1C3ED97C-3A76-4a2c-8D51-CE28D7131DD4}");
    assertEquals("", TaggedValueUtils.getTaggedValueValue(vaerdi2, taggedValueNameTestTag));

    Element dkKodeliste1 = repository.GetElementByGuid("{91A4860B-5771-4091-9337-7DCBF2DD3FAC}");
    assertEquals("test value DKKodeliste1",
        TaggedValueUtils.getTaggedValueValue(dkKodeliste1, taggedValueNameTestTag));

    Connector erRelateretTil =
        repository.GetConnectorByGuid("{0D467508-0484-4a26-B670-7A54ABB7F73D}");
    assertEquals("test value erRelateretTil",
        TaggedValueUtils.getTaggedValueValue(erRelateretTil, taggedValueNameTestTag));

  }

}
