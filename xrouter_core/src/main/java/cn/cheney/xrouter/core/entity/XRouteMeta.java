package cn.cheney.xrouter.core.entity;

import java.lang.reflect.Type;
import java.util.Map;

public class XRouteMeta {

    private String type;

    private String path;

    private String module;

    private Class className;

    private Map<String, Type> paramsTypeMap;

    public XRouteMeta(String type, Class className, String module, String path) {
        this.type = type;
        this.path = path;
        this.module = module;
        this.className = className;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getModule() {
        return module;
    }

    public Class getClassName() {
        return className;
    }

    public Map<String, Type> getParamsTypeMap() {
        return paramsTypeMap;
    }
}
