package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.DiagramDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Diagram;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link DiagramDao}.
 */
public class DiagramDaoImpl implements DiagramDao {

  private EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public DiagramDaoImpl(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public List<Diagram> findAllByPackageGuid(String packageGuid) throws ModellingToolsException {
    List<Diagram> diagrams = new ArrayList<>();
    for (org.sparx.Diagram eaDiagram : eaWrapper.getPackageByGuid(packageGuid).GetDiagrams()) {
      diagrams.add(new Diagram(eaDiagram));
    }
    return diagrams;
  }

}
