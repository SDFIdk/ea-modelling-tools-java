package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.DiagramDao;
import dk.gov.data.modellingtools.model.Diagram;
import java.util.ArrayList;
import java.util.List;
import org.sparx.Package;

/**
 * Implementation of {@link DiagramDao}.
 */
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
