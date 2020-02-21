package cn.cheney.xrouter.core.invok;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import cn.cheney.xrouter.core.constant.RouteType;


public abstract class Invokable {

    String path;
    RouteType type;
    String module;
    Class<?> clazz;


    public Object invoke(@Nullable Context context, @NonNull Map<String, Object> params) {
        return null;
    }

    public Object invoke(@Nullable Context context, @NonNull Map<String, Object> params,
                         int requestCode, int enterAnim, int exitAnim, String action) {
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

