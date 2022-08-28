package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.List;

public class SelectClause extends BaseClause {
    protected SelectClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof SelectClause) {
            sql = clause.getSql();
            sqlVars = clause.getSqlVars();
        }
        return this;
    }

    public static Clause of(String table, String... fields) {
        var fs = String.join(", ", fields);
        var sql = String.format("SELECT %s FROM %s", fs, table);
        return new SelectClause(sql, null);
    }
}
