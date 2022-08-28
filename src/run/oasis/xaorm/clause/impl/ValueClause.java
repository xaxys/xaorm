package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValueClause extends BaseClause {
    protected ValueClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof ValueClause) {
            sql = String.join(", ", sql, clause.getSql());
            sqlVars.addAll(clause.getSqlVars());
        }
        return this;
    }

    public static BaseClause of(List<Object> values) {
        var sb = new StringBuilder();
        sb.append("VALUES ");
        var bindStr = String.join(", ", Collections.nCopies(values.size(), "?"));
        sb.append(String.format("(%s)", bindStr));
        return new ValueClause(sb.toString(), values);
    }
}
