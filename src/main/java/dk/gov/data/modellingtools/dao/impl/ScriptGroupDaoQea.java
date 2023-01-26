package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;

/**
 * DAO for scripts groups in an QEA Enterprise Architect model.
 */
public class ScriptGroupDaoQea extends AbstractScriptGroupDao {

  public ScriptGroupDaoQea(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected String getSqlQueryForRetrievingScriptGroupsAndScripts(String scriptGroupNameOrRegex) {
    String sqlQuery =
        "select s.ScriptCategory, s.ScriptName, s.ScriptAuthor, s.Notes, s.Script from t_script s"
            + " where s.Script like '" + scriptGroupNameOrRegex
            + "' and s.Notes like '<Group%' union all"
            + " select s.ScriptCategory, s.ScriptName, s.ScriptAuthor, s.Notes, s.Script"
            + " from t_script s inner join t_script s1 on s1.ScriptName = s.ScriptAuthor"
            + " where s1.script like '" + scriptGroupNameOrRegex + "' and s1.Notes like '<Group%'"
            + " order by ScriptCategory, ScriptName";
    return sqlQuery;
  }

}
