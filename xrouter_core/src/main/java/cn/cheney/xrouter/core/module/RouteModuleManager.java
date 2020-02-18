package cn.cheney.xrouter.core.module;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import cn.cheney.xrouter.core.constant.GenerateFileConstant;
import cn.cheney.xrouter.core.entity.XRouteMeta;
import cn.cheney.xrouter.core.util.Logger;

public class RouteModuleManager {

    private Map<String, BaseModule> mModulesMap;

    public RouteModuleManager() {
        this.mModulesMap = new HashMap<>();
    }


    private BaseModule getModule(String moduleName) {
        if (TextUtils.isEmpty(moduleName)) {
            return null;
        }
        BaseModule baseModule = mModulesMap.get(moduleName);
        if (null == baseModule) {
            try {
                baseModule = (BaseModule) Class.forName(GenerateFileConstant.MODULE_CLASS_PREFIX +
                        moduleName).newInstance();
                mModulesMap.put(moduleName, baseModule);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Logger.d("getModule  module name=" + moduleName + " entity=" + baseModule);
        return baseModule;
    }

    public XRouteMeta getRouteMeta(String moduleName, String uriPath) {
        BaseModule module = getModule(moduleName);
        if (null == module) {
            Logger.w("getRouteMeta  module name=" + moduleName + " not found ");
            return null;
        }
        return module.getRouteMeta(uriPath);
    }

}
