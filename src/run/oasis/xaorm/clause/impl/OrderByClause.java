package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.List;

public class OrderByClause extends BaseClause {
    protected OrderByClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof OrderByClause) {
            sql = clause.getSql();
            sqlVars = clause.getSqlVars();
        }
        return null;
    }

    public static BaseClause of(String expr) {
        return new OrderByClause(String.format("ORDER BY %s", expr), null);
    }
}
