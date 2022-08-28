package run.oasis.xaorm.util;

public class StringHelper {
    public static String toCamelCase(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == ' ' || c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String toSnakeCase(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String notEmpty(boolean condition, String s) {
        return notEmpty(condition, s, "");
    }

    public static String notEmpty(String s) {
        return notEmpty(s != null && s.length() > 0, s);
    }

    public static String notEmpty(String s, String defaultValue) {
        return notEmpty(s != null && s.length() > 0, s, defaultValue);
    }

    public static String notEmpty(boolean condition, String s, String defaultValue) {
        return condition ? s : defaultValue;
    }
}
