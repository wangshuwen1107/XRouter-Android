package cn.cheney.xrouter.plugin.consts;

import java.io.File;


public interface PluginConstant {
    /**
     * 包名
     */
    String ROUTER_PACKAGE_NAME = "cn.cheney.xrouter";

    /**
     * 路由模块前缀
     */
    String MODULE_FILE_PREFIX = "XRouterModule_";
    /**
     * 路由的包名 cn/cheney/xrouter
     */
    String ROUTER_MODULE_CLASS_DIR = ROUTER_PACKAGE_NAME.replace('.', File.separatorChar);

    /**
     * 路由模块的全包名前缀
     */
    String MODULE_CLASS_PREFIX = ROUTER_PACKAGE_NAME + "." + MODULE_FILE_PREFIX;

    /**
     * 路由模块汇总类
     */
    String ALL_ROUTER_LOAD_CLASS = ROUTER_PACKAGE_NAME + "." + MODULE_FILE_PREFIX + "Loader";
}
