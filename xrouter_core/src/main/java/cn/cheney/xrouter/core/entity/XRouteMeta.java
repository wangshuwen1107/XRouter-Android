package cn.cheney.xrouter.core.entity;

import android.app.Activity;

import java.lang.reflect.Type;
import java.util.Map;

import cn.cheney.xrouter.core.constant.RouteType;

public class XRouteMeta {

    private RouteType type;

    private String path;

    private String module;

    private Class<? extends Activity> className;

    private Map<String, Type> paramsTypeMap;

    public XRouteMeta(RouteType type, Class<? extends Activity> className, String module, String path) {
        this.type = type;
        this.path = path;
        this.module = module;
        this.className = className;
    }

    public RouteType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getModule() {
        return module;
    }

    public Class<? extends Activity> getClassName() {
        return className;
    }

    public Map<String, Type> getParamsTypeMap() {
        return paramsTypeMap;
    }
}
