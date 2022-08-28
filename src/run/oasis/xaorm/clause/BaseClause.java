package run.oasis.xaorm.clause;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseClause implements Clause {
    protected String sql;
    protected List<Object> sqlVars;

    protected BaseClause(String sql, List<Object> sqlVars) {
        this.sql = sql == null ? "" : sql;
        this.sqlVars = sqlVars == null ? new ArrayList<>() : sqlVars;
    }

    public String getSql() {
        return sql;
    }

    public List<Object> getSqlVars() {
        return sqlVars;
    }

    public abstract Clause merge(Clause clause);
}
