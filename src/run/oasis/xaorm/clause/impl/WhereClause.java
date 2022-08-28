package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.ArrayList;
import java.util.List;

public class WhereClause extends BaseClause {
    protected WhereClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof WhereClause) {
            sql = String.join(" AND ", sql, clause.getSql().substring(6)); // remove prefix "WHERE "
            sqlVars.addAll(clause.getSqlVars());
        }
        return null;
    }

    public static BaseClause of(String expr, Object... vars) {
        var sql = String.format("WHERE %s", expr);
        return new WhereClause(sql, new ArrayList<>(List.of(vars)));
    }
}
