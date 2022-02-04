package dk.gov.data.modellingtools.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Basic logical data model.
 */
public class LogicalDataModel {

  private String name;

  private String version;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("name", name).append("version", version).toString();
  }

}
