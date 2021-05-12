package dk.gov.data.geo.datamodellingtools.model;

import dk.gov.data.geo.datamodellingtools.exception.DataModellingToolsException;
import dk.gov.data.geo.datamodellingtools.utils.XmlAndXsltUtils;

public class Diagram {

  private org.sparx.Diagram diagram;

  public Diagram(org.sparx.Diagram diagram) {
    super();
    this.diagram = diagram;
  }

  public String getName() {
    return diagram.GetName();
  }

  public void setName(String name) {
    diagram.SetName(name);
  }

  public String getNotes() {
    return diagram.GetNotes();
  }

  /**
   * The notes come out as HTML, but without root node. A arbitrary root node must be added for
   * further XML processing.
   */
  public void setNotes(String notes) {
    diagram.SetNotes(notes);
  }

  public String getNotesAsAsciidoc() throws DataModellingToolsException {
    return XmlAndXsltUtils.transformXml("<note>" + getNotes() + "</note>",
        "/export/transform_ea_notes_to_asciidoc.xsl");
  }


}
