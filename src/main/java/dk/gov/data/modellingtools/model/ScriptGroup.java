package dk.gov.data.modellingtools.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Script group.
 * 
 * <p>In EA, a script is developed within a script group, the properties of which determine how that
 * script is to be made available to the user</p>
 */
public class ScriptGroup {

  private String id;

  private String name;

  private String notes;

  private List<Script> scripts;

  /**
   * Create a new script group with the given parameters.
   */
  public ScriptGroup(String id, String name, String notes) {
    super();
    this.id = id;
    this.name = name;
    this.notes = notes;
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

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<Script> getScripts() {
    return scripts;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setScripts(List<Script> scripts) {
    this.scripts = scripts;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id)
        .append("name", name).toString();
  }

}
