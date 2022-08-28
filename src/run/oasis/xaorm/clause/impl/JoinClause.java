package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.ArrayList;
import java.util.List;

public class JoinClause extends BaseClause {
    protected JoinClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof JoinClause) {
            sql = String.join(" ", sql, clause.getSql());
            sqlVars.addAll(clause.getSqlVars());
        }
        return null;
    }

    public static BaseClause of(String table, String expr, Object... vars) {
        var sql = String.format("JOIN %s on %s", table, expr);
        return new JoinClause(sql, new ArrayList<>(List.of(vars)));
    }

    public static BaseClause left(String table, String expr, Object... vars) {
        var sql = String.format("LEFT JOIN %s on %s", table, expr);
        return new JoinClause(sql, new ArrayList<>(List.of(vars)));
    }

    public static BaseClause right(String table, String expr, Object... vars) {
        var sql = String.format("RIGHT JOIN %s on %s", table, expr);
        return new JoinClause(sql, new ArrayList<>(List.of(vars)));
    }

    public static BaseClause outer(String table, String expr, Object... vars) {
        var sql = String.format("OUTER JOIN %s on %s", table, expr);
        return new JoinClause(sql, new ArrayList<>(List.of(vars)));
    }
}
