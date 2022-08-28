package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateClause extends BaseClause {
    protected UpdateClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof UpdateClause) {
            sql = clause.getSql();
            sqlVars = clause.getSqlVars();
        }
        return null;
    }

    public static BaseClause of(String table, Map<String, Object> values) {
        var valuePlaceholders = values.entrySet().stream()
                .map(e -> String.format("%s = ?", e.getKey()))
                .collect(Collectors.joining(", "));
        var sql = String.format("UPDATE %s SET %s", table, valuePlaceholders);
        var sqlVars = values.values().stream().toList();
        return new UpdateClause(sql, sqlVars);
    }
}
