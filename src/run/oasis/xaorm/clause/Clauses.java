package run.oasis.xaorm.clause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clauses extends BaseClause {

    public Clauses() {
        super("", null);
    }

    public enum Type {
        INSERT,
        VALUES,
        SELECT,
        LIMIT,
        WHERE,
        JOIN,
        ORDER_BY,
        UPDATE,
        DELETE,
        COUNT,
    }

    private final static Map<String, Type> typeMap = new HashMap<>(
            Map.of(
                    "InsertClause", Type.INSERT,
                    "ValueClause", Type.VALUES,
                    "SelectClause", Type.SELECT,
                    "LimitClause", Type.LIMIT,
                    "WhereClause", Type.WHERE,
                    "JoinClause", Type.JOIN,
                    "OrderByClause", Type.ORDER_BY,
                    "UpdateClause", Type.UPDATE,
                    "DeleteClause", Type.DELETE,
                    "CountClause", Type.COUNT
            )
    );

    private Map<Type, Clause> sqlMap = new HashMap<>();

    private void set(Type type, Clause clause) {
        var cls = sqlMap.get(type);
        if (cls != null) {
            cls.merge(clause);
        } else {
            sqlMap.put(type, clause);
        }
    }

    public Clause merge(Clause clause) {
        var type = typeMap.get(clause.getClass().getSimpleName());
        if (type != null) {
            set(type, clause);
        }
        return this;
    }

    public void clear() {
        sql = "";
        sqlVars = new ArrayList<>();
    }

    public Clause build(Type... orders) {
        clear();
        for (var order : orders) {
            var clause = sqlMap.get(order);
            if (clause != null) {
                sql = String.join(" ", sql, clause.getSql());
                sqlVars.addAll(clause.getSqlVars());
            }
        }
        return this;
    }
}
