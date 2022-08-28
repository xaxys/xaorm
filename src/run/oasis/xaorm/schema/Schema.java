package run.oasis.xaorm.schema;

import run.oasis.xaorm.util.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema {

    private Class clazz;
    private String name;
    private List<Field> fields = new ArrayList<>();
    private Map<String, Field> fieldMap = new HashMap<>();
    private List<String> primaryKeys = new ArrayList<>();

    public Class getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getPrimaryKey() {
        return primaryKeys.isEmpty() ? null : String.format("PRIMARY KEY (%s)", String.join(", ", primaryKeys));
    }

    public String[] getFieldNames(String... names) {
        return fields.stream().map(Field::getName).toArray(String[]::new);
    }

    public Field getField(String name) {
        return fieldMap.get(name);
    }

    public List<Object> recordValues(Object obj) {
        var cls = obj.getClass();
        var values = new ArrayList<>();
        for (var field : fields) {
            try {
                var fieldName = StringHelper.toCamelCase(field.getName());
                var declField = cls.getDeclaredField(fieldName);
                declField.setAccessible(true);
                values.add(declField.get(obj));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }
        return values;
    }

    public static Schema of(Class clazz) {
        var schema = new Schema();
        schema.clazz = clazz;
        schema.name = StringHelper.toSnakeCase(clazz.getSimpleName());
        for (var field : clazz.getDeclaredFields()) {
            try {
                var f = Field.of(field);
                schema.fields.add(f);
                schema.fieldMap.put(f.getName(), f);
                if (f.isPrimaryKey()) {
                    schema.primaryKeys.add(f.getName());
                }
            } catch (Exception e) {
                continue;
            }
        }
        return schema;
    }

}
