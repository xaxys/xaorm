package run.oasis.xaorm.clause;

import java.util.List;

public interface Clause {
    String getSql();
    List<Object> getSqlVars();
    Clause merge(Clause clause);
}
