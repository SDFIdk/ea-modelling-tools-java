package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;

/**
 * Retrieves scripts group from an EAPX Enterprise Architect model (not supported in 64-bit EA 16).
 */
public class ScriptGroupDaoEapx extends AbstractScriptGroupDao {

  public ScriptGroupDaoEapx(EnterpriseArchitectWrapper eaWrapper) {
    super(eaWrapper);
  }

  @Override
  protected String getSqlQueryForRetrievingScriptGroupsAndScripts(String scriptGroupNameOrRegex) {
    /*
     * Requires probably Jet 4.0 (see EA settings).
     * 
     * From the JScript used earlier: For some reason, union all gives an error when using
     * Repository.SQLQuery, therefore two queries instead. The error: EA shows an error box with the
     * following contents: Error: Code=0x0 Source Line : 0; Char : 0 Error description = (null)
     */
    String sqlQuery =
        "select s.ScriptCategory, s.ScriptName, s.ScriptAuthor, s.Notes, s.Script from t_script s"
            + " where s.Script like '" + scriptGroupNameOrRegex
            + "' and s.Notes like '<Group*' union all"
            + " select s.ScriptCategory, s.ScriptName, s.ScriptAuthor, s.Notes, s.Script"
            + " from t_script s inner join t_script s1 on s1.ScriptName = s.ScriptAuthor"
            + " where s1.script like '" + scriptGroupNameOrRegex + "' and s1.Notes like '<Group*'"
            + " order by ScriptCategory, ScriptName";
    return sqlQuery;
  }

}
