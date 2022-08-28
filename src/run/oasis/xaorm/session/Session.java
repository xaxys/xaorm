package run.oasis.xaorm.session;

import run.oasis.xaorm.Engine;
import run.oasis.xaorm.clause.Clauses;
import orm.clause.impl.*;
import run.oasis.xaorm.clause.impl.*;
import run.oasis.xaorm.schema.Field;
import run.oasis.xaorm.schema.Schema;
import run.oasis.xaorm.util.StringHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Session {

    private Engine engine;
    private Connection conn;
    private Schema schema;
    private Clauses clause = new Clauses();
    private StringBuilder sql = new StringBuilder();
    private List<Object> sqlVars = new ArrayList<>();

    public Session(Engine engine) {
        this.engine = engine;
        this.conn = engine.getConn();
    }

    public Connection getConn() {
        return conn;
    }

    public Schema getSchema() {
        if (schema == null) {
            engine.getLog().log(Level.WARNING, "schema is not set");
        }
        return schema;
    }

    public Session model(Object model) {
        return schema(model.getClass());
    }

    public Session schema(Class<?> clazz) {
        schema = Schema.of(clazz);
        return this;
    }

    public void clear() {
        sql = new StringBuilder();
        sqlVars.clear();
        clause.clear();
    }

    public Session raw(String sql) {
        this.sql.append(sql);
        return this;
    }

    public Session raw(String sql, List<?> values) {
        this.sql.append(sql);
        this.sqlVars.addAll(values);
        return this;
    }

    public int exec() {
        engine.getLog().log(Level.INFO, String.format("%s; %s", sql, sqlVars));
        try {
            var stmt = conn.prepareStatement(sql.toString());
            for (var i = 1; i <= sqlVars.size(); i++) {
                stmt.setObject(i, sqlVars.get(i-1));
            }
            var result = stmt.executeUpdate();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            clear();
        }
        return 0;
    }

    public ResultSet query() {
        engine.getLog().log(Level.INFO, String.format("%s; %s", sql, sqlVars));
        try {
            var stmt = conn.prepareStatement(sql.toString());
            for (var i = 1; i <= sqlVars.size(); i++) {
                stmt.setObject(i, sqlVars.get(i-1));
            }
            var result = stmt.executeQuery();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            clear();
        }
        return null;
    }

    public boolean createTable() {
        var table = schema;
        var columns = table.getFields().stream().map(Field::toString).collect(ArrayList<String>::new, ArrayList::add, ArrayList::addAll);
        var primaryKey = table.getPrimaryKey();
        if (primaryKey != null) {
            columns.add(primaryKey);
        }
        var description = String.join(", ", columns);
        var sql = String.format("CREATE TABLE IF NOT EXISTS %s (%s)", schema.getName(), description);
        var result = raw(sql).exec();
        return result == 1;
    }

    public boolean dropTable() {
        var sql = String.format("DROP TABLE IF EXISTS %s", schema.getName());
        var result = raw(sql).exec();
        return result == 1;
    }

    public int insert(Object... models) {
        var recordValues = new ArrayList<>();
        for (var model : models) {
            var table = this.model(model).getSchema();
            clause.merge(InsertClause.of(table.getName(), table.getFieldNames()));
            recordValues.addAll(schema.recordValues(model));
        }

        clause.merge(ValueClause.of(recordValues));
        clause.build(Clauses.Type.INSERT, Clauses.Type.VALUES);
        var result = raw(clause.getSql(), clause.getSqlVars()).exec();
        return result;
    }

    public int update(String key, Object value) {
        return update(Map.of(key, value));
    }

    public int update(Map<String, Object> kv) {
        clause.merge(UpdateClause.of(schema.getName(), kv));
        clause.build(Clauses.Type.UPDATE, Clauses.Type.WHERE);
        var result = raw(clause.getSql(), clause.getSqlVars()).exec();
        return result;
    }

    public int delete() {
        clause.merge(DeleteClause.of(schema.getName()));
        clause.build(Clauses.Type.DELETE, Clauses.Type.WHERE);
        var result = raw(clause.getSql(), clause.getSqlVars()).exec();
        return result;
    }

    public int count() throws SQLException {
        clause.merge(CountClause.of(schema.getName()));
        clause.build(Clauses.Type.SELECT, Clauses.Type.WHERE);
        var result = raw(clause.getSql(), clause.getSqlVars()).query();
        if (result.next()) {
            return result.getInt(1);
        }
        return 0;
    }

    public <T> List<T> findAll() {
        clause.merge(SelectClause.of(schema.getName(), schema.getFieldNames()));
        clause.build(Clauses.Type.SELECT, Clauses.Type.JOIN, Clauses.Type.WHERE, Clauses.Type.ORDER_BY, Clauses.Type.LIMIT);
        var result = raw(clause.getSql(), clause.getSqlVars()).query();

        var clazz = schema.getClazz();
        var list = new ArrayList<T>();
        try {
            while (result.next()) {
                var model = (T) clazz.getDeclaredConstructor().newInstance();
                for (var field : schema.getFields()) {
                    var dbValue = result.getObject(field.getName());
                    var fieldName = StringHelper.toCamelCase(field.getName());
                    var reflectField = clazz.getDeclaredField(fieldName);
                    reflectField.setAccessible(true);
                    reflectField.set(model, dbValue);
                }
                list.add(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public <T> T findOne() {
        clause.merge(LimitClause.of(1));
        var result = findAll();
        return result.size() > 0 ? (T) result.get(0) : null;
    }

    public Session limit(int limit) {
        clause.merge(LimitClause.of(limit));
        return this;
    }

    public Session where(String field, Object... value) {
        clause.merge(WhereClause.of(field, value));
        return this;
    }

    public Session orderBy(String field) {
        clause.merge(OrderByClause.of(field));
        return this;
    }

    public Session join(String table, String field, String joinField) {
        clause.merge(JoinClause.of(table, field, joinField));
        return this;
    }
}
