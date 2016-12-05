package org.hddframework.mybatis.locker.gen;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SongWei on 24/11/2016.
 */
public class DefaultValueGenerator {

    private static final String[] INTEGER = new String[]{"byte", "short", "int", "long", "java.lang.Byte",
            "java.lang.Short", "java.lang.Integer", "java.lang.Long"};
    private static final String[] FLOAT = new String[]{"float", "double", "java.lang.Double", "java.lang.Float"};
    private static final String[] STRING = new String[]{"string", "java.lang.String"};
    private static final String[] DATE = new String[]{"date", "java.util.Date", "java.sql.Date"};
    private static final String[] TIMESTAMP = new String[]{"timestamp", "java.sql.Timestamp"};

    private static final Pattern PATTERN_MNUMBER = Pattern.compile("-?[0-9]+.*[0-9]*");

    public static boolean isNumber(Object object) {
        Matcher m = PATTERN_MNUMBER.matcher(object.toString());
        return m.matches();
    }

    public static boolean isNumber(String str) {
        Matcher m = PATTERN_MNUMBER.matcher(str);
        return m.matches();
    }

    public static String defaultStepping(Type type) {
        return defaultStepping(type.getTypeName());
    }

    public static String defaultStepping(String type) {
        for (String str : INTEGER) {
            if (str.equalsIgnoreCase(type)) {
                return "1";
            }
        }
        for (String str : FLOAT) {
            if (str.equalsIgnoreCase(type)) {
                return "0.1";
            }
        }
        for (String str : STRING) {
            if (str.equalsIgnoreCase(type)) {
                return UUID.randomUUID().toString().replaceAll("-", "");
            }
        }
        return null;
    }


    public static String defaultValue(Type type) {
        return defaultValue(type.getTypeName());
    }

    public static String defaultValue(String type) {
        for (String str : INTEGER) {
            if (str.equalsIgnoreCase(type)) {
                return "0";
            }
        }
        for (String str : FLOAT) {
            if (str.equalsIgnoreCase(type)) {
                return "0.0";
            }
        }
        for (String str : STRING) {
            if (str.equalsIgnoreCase(type)) {
                return UUID.randomUUID().toString().replaceAll("-", "");
            }
        }
        for (String str : DATE) {
            if (str.equalsIgnoreCase(type)) {
                return null;
            }
        }
        for (String str : TIMESTAMP) {
            if (str.equalsIgnoreCase(type)) {
                return null;
            }
        }
        return null;
    }

}
