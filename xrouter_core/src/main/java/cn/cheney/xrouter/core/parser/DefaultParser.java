package cn.cheney.xrouter.core.parser;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

public class DefaultParser implements ParamParser {

    @Override
    public Object parse(String paramName, String paramValue, Type paramType) {
        if (paramType == String.class) {
            return paramValue;
        } else if (paramType == boolean.class || paramType == Boolean.class) {
            return paramValue == null ? null : parseBoolean(paramValue);
        } else if (paramType == int.class || paramType == Integer.class) {
            return paramValue == null ? null : (int) parseLong(paramValue);
        } else if (paramType == long.class || paramType == Long.class) {
            return paramValue == null ? null : parseLong(paramValue);
        } else if (paramType == float.class || paramType == Float.class) {
            return paramValue == null ? null : Float.valueOf(paramValue);
        } else if (paramType == double.class || paramType == Double.class) {
            return paramValue == null ? null : Double.valueOf(paramValue);
        } else if (paramType == String[].class) {
            return JSON.parseArray(paramValue, String.class).toArray();
        } else if (paramType == int[].class) {
            return JSON.parseArray(paramValue, int.class).toArray();
        } else if (paramType == float[].class) {
            return JSON.parseArray(paramValue, float.class).toArray();
        } else if (paramType == double[].class) {
            return JSON.parseArray(paramValue, double.class).toArray();
        } else if (paramType == long[].class) {
            return JSON.parseArray(paramValue, long.class).toArray();
        }
        return JSON.parseObject(paramValue, paramType);
    }

    private Boolean parseBoolean(String text) {
        if (text.equalsIgnoreCase("true")
                || text.equalsIgnoreCase("1")) {
            return Boolean.TRUE;
        } else if (text.equalsIgnoreCase("false")
                || text.equalsIgnoreCase("0")) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("cannot parse to Boolean");
    }


    private long parseLong(String text) {
        if (text.endsWith(".0")) text = text.substring(0, text.length() - 2);
        return Long.parseLong(text);
    }


}
