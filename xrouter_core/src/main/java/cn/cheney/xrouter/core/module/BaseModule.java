package cn.cheney.xrouter.core.module;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import cn.cheney.xrouter.core.invok.Invokable;

import static cn.cheney.xrouter.constant.GenerateFileConstant.MODULE_CLASS_PREFIX;


public abstract class BaseModule {

    private String moduleName;

    private Map<String, Invokable<?>> mMethodsMap = new HashMap<>();

    public BaseModule() {
        moduleName = getName();
        if (TextUtils.isEmpty(moduleName)) {
            this.moduleName = generateDefaultModuleName();
        }
        load(mMethodsMap);
    }


    public abstract String getName();

    public abstract void load(Map<String, Invokable<?>> routeMap);


    public Invokable<?> getRouteMeta(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return mMethodsMap.get(path);
    }

    private String generateDefaultModuleName() {
        final String name = getClass().getName();
        if (name.startsWith(MODULE_CLASS_PREFIX)) {
            return name.substring(MODULE_CLASS_PREFIX.length());
        } else {
            return name;
        }
    }

}
