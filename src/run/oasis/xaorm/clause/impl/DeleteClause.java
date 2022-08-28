package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.List;

public class DeleteClause extends BaseClause {
    protected DeleteClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof DeleteClause) {
            sql = clause.getSql();
            sqlVars = clause.getSqlVars();
        }
        return this;
    }

    public static Clause of(String table) {
        var sql = String.format("DELETE FROM %s", table);
        return new DeleteClause(sql, null);
    }
}
