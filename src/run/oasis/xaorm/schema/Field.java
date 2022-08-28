package run.oasis.xaorm.schema;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static run.oasis.xaorm.util.StringHelper.notEmpty;
import static run.oasis.xaorm.util.StringHelper.toSnakeCase;

public class Field {
    private String name;
    private String type;
    private String params;
    private boolean primaryKey;

    private Column annotation;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getParams() {
        return params;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", name, type, params).trim();
    }

    public static Field of(java.lang.reflect.Field reflectField) throws Exception {
        var field = new Field();
        field.annotation = reflectField.getAnnotation(Column.class);
        field.name = toSnakeCase(reflectField.getName());
        field.type = dataTypeOf(reflectField.getType().getName());

        if (field.annotation != null) {
            field.name = notEmpty(field.annotation.name(), field.name);
            field.type = notEmpty(field.annotation.type(), field.type);
            field.primaryKey = field.annotation.primaryKey();

            var sb = new StringBuilder();
            sb.append(notEmpty(!field.annotation.nullable(), "NOT NULL ", ""));
            sb.append(notEmpty(field.annotation.unique(), "UNIQUE ", ""));
            sb.append(notEmpty(field.annotation.autoIncrement(), "AUTO_INCREMENT ", ""));
            sb.append(notEmpty(field.annotation.raw()));
            field.params = sb.toString().trim();
        }

        return field;
    }

    private static final Map<String, String> typeMap = new HashMap<>() {{
        put("char", "INT");
        put("byte", "INT");
        put("short", "INT");
        put("int", "INT");
        put("long", "BIGINT");
        put("float", "FLOAT");
        put("double", "DOUBLE");
        put("boolean", "BOOL");
        put("java.lang.String", "TEXT");
        put("java.time.LocalDateTime", "DATETIME");
        put("java.sql.Date", "DATE");
        put("java.sql.Time", "TIME");
        put("java.sql.Timestamp", "TIMESTAMP");

    }};

    private static String dataTypeOf(String s) throws Exception {
        var type = typeMap.get(s);
        if (type == null) {
            throw new Exception("Unsupported data type: " + s);
        }
        return type;
    }
}
