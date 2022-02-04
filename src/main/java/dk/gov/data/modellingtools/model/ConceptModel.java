package dk.gov.data.modellingtools.model;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ConceptModel {

  private URI identifier;
  private Map<String, String> titles;
  private Map<String, String> descriptions;
  private URI[] languages;
  private String responsibleEntity;
  private URI publisher;
  private String version;
  private Date lastModifiedDate;
  private String modelStatus;
  private String approvalStatus;
  private String approvingAuthority;
  private String modelScope;
  private URI[] themes;
  private URI[] sources;
  private Map<String, String> versionNotes;

  public ConceptModel() {
    super();
  }

  public URI getIdentifier() {
    return identifier;
  }

  public void setIdentifier(URI identifier) {
    this.identifier = identifier;
  }

  public Map<String, String> getTitles() {
    return titles;
  }

  public void setTitles(Map<String, String> titles) {
    this.titles = titles;
  }

  public Map<String, String> getDescriptions() {
    return descriptions;
  }

  public void setDescriptions(Map<String, String> descriptions) {
    this.descriptions = descriptions;
  }

  public URI[] getLanguages() {
    return ArrayUtils.clone(languages);
  }

  public void setLanguages(URI[] languages) {
    this.languages = ArrayUtils.clone(languages);
  }

  public String getResponsibleEntity() {
    return responsibleEntity;
  }

  public void setResponsibleEntity(String responsibleEntity) {
    this.responsibleEntity = responsibleEntity;
  }

  public URI getPublisher() {
    return publisher;
  }

  public void setPublisher(URI publisher) {
    this.publisher = publisher;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Date getLastModifiedDate() {
    if (lastModifiedDate == null) {
      return null;
    } else {
      return new Date(lastModifiedDate.getTime());
    }
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    if (lastModifiedDate == null) {
      this.lastModifiedDate = null;
    } else {
      this.lastModifiedDate = new Date(lastModifiedDate.getTime());
    }
  }

  public String getModelStatus() {
    return modelStatus;
  }

  public void setModelStatus(String modelStatus) {
    this.modelStatus = modelStatus;
  }

  public String getApprovalStatus() {
    return approvalStatus;
  }

  public void setApprovalStatus(String approvalStatus) {
    this.approvalStatus = approvalStatus;
  }

  public String getApprovingAuthority() {
    return approvingAuthority;
  }

  public void setApprovingAuthority(String approvingAuthority) {
    this.approvingAuthority = approvingAuthority;
  }

  public String getModelScope() {
    return modelScope;
  }

  public void setModelScope(String modelScope) {
    this.modelScope = modelScope;
  }

  public URI[] getThemes() {
    return ArrayUtils.clone(themes);
  }

  public void setThemes(URI[] themes) {
    this.themes = ArrayUtils.clone(themes);
  }

  public URI[] getSources() {
    return ArrayUtils.clone(sources);
  }

  public void setSources(URI[] sources) {
    this.sources = ArrayUtils.clone(sources);
  }

  public Map<String, String> getVersionNotes() {
    return versionNotes;
  }

  public void setVersionNotes(Map<String, String> versionNotes) {
    this.versionNotes = versionNotes;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("title (da)", titles.get("da"))
        .append("version", version).toString();
  }

}
