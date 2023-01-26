package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.ScriptGroupDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;

/**
 * Dummy implementation of {@link ScriptGroupDao} use in testing.
 */
public class DummyScriptGroupDao extends AbstractScriptGroupDao {

  public DummyScriptGroupDao(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected String getSqlQueryForRetrievingScriptGroupsAndScripts(String scriptGroupNameOrRegex) {
    return "";
  }

}
