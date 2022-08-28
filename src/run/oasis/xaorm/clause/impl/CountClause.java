package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.List;

public class CountClause extends BaseClause {
    protected CountClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof CountClause) {
            sql = clause.getSql();
            sqlVars = clause.getSqlVars();
        }
        return null;
    }

    public static BaseClause of(String table) {
        var sql = String.format("SELECT COUNT(*) FROM %s", table);
        return new CountClause(sql, null);
    }
}
