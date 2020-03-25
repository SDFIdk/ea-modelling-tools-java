package dk.gov.data.geo.datamodellingtools.scriptmanagement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Script {

  private String id;
  private String name;
  private String language;
  private String scriptGroupId;
  private String contents;

  /**
   * Create a new script with the given parameters.
   */
  public Script(String id, String name, String language, String scriptGroupId, String contents) {
    super();
    this.id = id;
    this.name = name;
    this.language = language;
    this.scriptGroupId = scriptGroupId;
    this.contents = contents;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getScriptGroupId() {
    return scriptGroupId;
  }

  public void setScriptGroupId(String scriptGroupId) {
    this.scriptGroupId = scriptGroupId;
  }

  public String getContents() {
    return contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  /**
   * Returns the file extension, based on the language.
   */
  public String getFileExtension() {
    String scriptExtension;
    switch (language) {
      case "JScript":
      case "JavaScript":
        scriptExtension = ".js";
        break;
      case "VBScript":
        scriptExtension = ".vbs";
        break;
      case "":
        scriptExtension = ".txt";
        break;
      default:
        scriptExtension = "";
    }
    return scriptExtension;
  }

  public String getFileName() {
    return getName() + getFileExtension();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id)
        .append("name", name).append("language", language).append("script group id", scriptGroupId)
        .toString();
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    Script clone = new Script(id, name, language, scriptGroupId, contents);
    return clone;
  }

}
