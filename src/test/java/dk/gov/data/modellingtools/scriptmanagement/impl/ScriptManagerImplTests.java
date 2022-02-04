package dk.gov.data.modellingtools.scriptmanagement.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

@ExtendWith(MockitoExtension.class)
class ScriptManagerImplTests {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScriptManagerImplTests.class);

  private ScriptManagerImpl scriptManagerImpl;

  @Mock
  private EnterpriseArchitectWrapper eaWrapper;

  @Test
  void testExportScripts() throws IOException, ModellingToolsException, XPathExpressionException {

    File folderForTest = new File(FileUtils.getTempDirectory(), this.getClass().getSimpleName());
    LOGGER.debug("Testing in {}", folderForTest);
    if (folderForTest.mkdir()) {
      try {
        InputStream queryResultAsInputStream =
            this.getClass().getResourceAsStream("/scriptmanager/queryresult.xml");
        String queryResult = IOUtils.toString(queryResultAsInputStream, StandardCharsets.UTF_8);

        when(eaWrapper.sqlQuery(anyString())).thenReturn(queryResult);

        scriptManagerImpl = new ScriptManagerImpl(eaWrapper, null);
        scriptManagerImpl.exportScripts("A normal script group", folderForTest, false);
        Comparator<File> alphabeticalFileNameComparator = new AlphabeticalFileNameComparator();
        File[] scriptGroupFolderList = folderForTest.listFiles();
        Arrays.sort(scriptGroupFolderList, alphabeticalFileNameComparator);
        Validate.isTrue(scriptGroupFolderList.length == 2);
        Validate.isTrue("A normal script group".equals(scriptGroupFolderList[0].getName()));
        Validate.isTrue("referencedata_scripts.xml".equals(scriptGroupFolderList[1].getName()));
        File[] fileList = scriptGroupFolderList[0].listFiles();
        Arrays.sort(fileList, alphabeticalFileNameComparator);
        testExportScriptsValidateNumberOfFiles(fileList);
        testExportScriptsValidateFileNames(fileList);
        testExportScriptsCompareScriptWithNonAsciiCharacters(fileList[2], queryResult);
      } finally {
        FileUtils.deleteDirectory(folderForTest);
      }
    } else {
      fail("Could not create test directory");
    }

  }

  private void testExportScriptsValidateNumberOfFiles(File... fileList) {
    assertEquals(3, fileList.length, "Expected 3 files");
  }

  private void testExportScriptsCompareScriptWithNonAsciiCharacters(File file, String queryResult)
      throws IOException, XPathExpressionException {
    List<String> actualContentsLines = FileUtils.readLines(file, StandardCharsets.UTF_8);
    doSpotCheck(actualContentsLines);
    doCompleteCheck(queryResult, actualContentsLines);
  }

  private void doCompleteCheck(String queryResult, List<String> actualContentsLines)
      throws XPathExpressionException {
    List<String> expectedContentsLines = retrieveExpectedContentsLinesWithXpath(queryResult);
    assertEquals(expectedContentsLines.size(), actualContentsLines.size());
    for (int i = 0; i < expectedContentsLines.size(); i++) {
      assertEquals(expectedContentsLines.get(i), actualContentsLines.get(i),
          "Difference in line content");
      LOGGER.debug(actualContentsLines.get(i));
    }
  }

  private void doSpotCheck(List<String> actualContentsLines) {
    // double check manually
    assertEquals("Æ Ø Å", actualContentsLines.get(6));
    assertEquals("æ ø å", actualContentsLines.get(7));
    assertEquals("いろはにほへとちりぬるをわかよたれそつねならむうゐのおくやまけふこえてあさきゆめみしゑひもせす", actualContentsLines.get(35));
  }

  private void testExportScriptsValidateFileNames(File... fileList) {
    assertEquals("A Jscript.js", fileList[0].getName());
    assertEquals("A VBScript.vbs", fileList[1].getName());
    assertEquals("JavaScript containing many different characters.js", fileList[2].getName());
  }

  private List<String> retrieveExpectedContentsLinesWithXpath(String queryResult)
      throws XPathExpressionException {
    // Default Xpath implementation, not Saxons
    XPath xpath = XPathFactory.newInstance().newXPath();
    String xpathExpression = "/EADATA/Dataset_0/Data/Row[4]/Script/text()";
    String expectedScript = (String) xpath.evaluate(xpathExpression,
        new InputSource(IOUtils.toInputStream(queryResult, StandardCharsets.UTF_8)),
        XPathConstants.STRING);
    // see https://stackoverflow.com/questions/454908/split-java-string-by-new-line
    List<String> expectedContentsLines = List.of(expectedScript.split("\\R", -1));
    return expectedContentsLines;
  }

  private static final class AlphabeticalFileNameComparator implements Comparator<File> {

    @Override
    public int compare(File file1, File file2) {
      return file1.getName().compareTo(file2.getName());
    }
  }

}
