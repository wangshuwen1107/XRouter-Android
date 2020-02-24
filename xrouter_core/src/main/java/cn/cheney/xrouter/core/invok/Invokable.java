package cn.cheney.xrouter.core.invok;

import androidx.annotation.NonNull;

import java.util.Map;

import cn.cheney.xrouter.core.constant.RouteType;


public abstract class Invokable<T> {

    String path;
    RouteType type;
    String module;
    Class<?> clazz;


    public T invoke(@NonNull Map<String, Object> params) {
        return null;
    }


    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

}

