package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.List;

public class InsertClause extends BaseClause {

    protected InsertClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof InsertClause) {
            sql = clause.getSql();
            sqlVars = clause.getSqlVars();
        }
        return null;
    }

    public static BaseClause of(String table, String... values) {
        var valueStr = String.join(", ", values);
        var sql = String.format("INSERT INTO %s (%s)", table, valueStr);
        return new InsertClause(sql, null);
    }
}
