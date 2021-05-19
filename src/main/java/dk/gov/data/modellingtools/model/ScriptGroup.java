package dk.gov.data.modellingtools.model;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ScriptGroup {

  private String id;

  private String name;

  private List<Script> scripts;

  /**
   * Create a new script group with the given parameters.
   */
  public ScriptGroup(String id, String name) {
    super();
    this.id = id;
    this.name = name;
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

  public List<Script> getScripts() {
    return scripts;
  }

  public void setScripts(List<Script> scripts) {
    this.scripts = scripts;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id)
        .append("name", name).toString();
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    ScriptGroup clone = new ScriptGroup(this.id, this.name);
    return clone;
  }

}
