package dk.gov.data.modellingtools.ea.model;

public enum ConnectorType {
  /*
   * Enum values must have the same name as in EA, but of course uppercase. See also findType and
   * see https://www.sparxsystems.com/search/sphider/search.php?query=%22connector%20class%22%
   * 20t_connector&type=and&category=User+Guide+Latest&tab=5&search=1 (the table on that page does
   * not seem to be complete though).
   * 
   * Not all connector types are present as enum value here (can be added if needed).
   */
  ASSOCIATION("Assocation"), AGGREGATION("Aggregation"), DEPENDENCY("Dependency"), GENERALIZATION(
      "Generalization"), MANIFEST(
          "Manifest"), NOTELINK("NoteLink"), PACKAGE("Package"), USAGE("Usage");

  private String eaType;

  ConnectorType(String eaType) {
    this.eaType = eaType;
  }

  public String getEaType() {
    return eaType;
  }

  public static ConnectorType findType(String eaType) {
    return ConnectorType.valueOf(eaType.toUpperCase());
  }

};
