package run.oasis.xaorm.clause.impl;

import run.oasis.xaorm.clause.BaseClause;
import run.oasis.xaorm.clause.Clause;

import java.util.ArrayList;
import java.util.List;

public class LimitClause extends BaseClause {
    protected LimitClause(String sql, List<Object> sqlVars) {
        super(sql, sqlVars);
    }

    public Clause merge(Clause clause) {
        if (clause instanceof LimitClause) {
            sql = clause.getSql();
            sqlVars = clause.getSqlVars();
        }
        return null;
    }

    public static Clause of(Object... values) {
        return new LimitClause("LIMIT ?", new ArrayList<>(List.of(values)));
    }
}
