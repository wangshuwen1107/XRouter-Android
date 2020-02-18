package cn.cheney.xrouter.core.module;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import cn.cheney.xrouter.core.entity.XRouteMeta;

import static cn.cheney.xrouter.core.constant.GenerateFileConstant.MODULE_CLASS_PREFIX;

public class BaseModule {

    private String moduleName;

    private Map<String, XRouteMeta> mMethodsMap = new HashMap<>();

    public BaseModule() {
        this(null);
    }

    public BaseModule(String moduleName) {
        if (TextUtils.isEmpty(moduleName)) {
            this.moduleName = generateDefaultModuleName();
        } else {
            this.moduleName = moduleName;
        }
        load(mMethodsMap);
    }

    protected void load(Map<String, XRouteMeta> routeMap) {
    }


    public XRouteMeta getRouteMeta(String path) {
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
