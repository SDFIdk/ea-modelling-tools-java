package dk.gov.data.geo.datamodellingtools.dao.impl;

import dk.gov.data.geo.datamodellingtools.dao.DiagramDao;
import dk.gov.data.geo.datamodellingtools.model.Diagram;
import java.util.ArrayList;
import java.util.List;
import org.sparx.Package;

public class DiagramDaoImpl implements DiagramDao {

  @Override
  public List<Diagram> findAll(Package umlPackage) {
    List<Diagram> diagrams = new ArrayList<>();
    for (org.sparx.Diagram eaDiagram : umlPackage.GetDiagrams()) {
      diagrams.add(new Diagram(eaDiagram));
    }
    return diagrams;
  }

}
