package cn.cheney.xrouter.plugin.consts;

import java.io.File;

public interface XRouterConstant {

    String MODULE_CLASS_PACKAGE = "cn.cheney.xrouter";

    String MODULE_CLASS_DIR = MODULE_CLASS_PACKAGE.replace('.', File.separatorChar);

    String MODULE_CLASS_PREFIX = MODULE_CLASS_PACKAGE + ".XRoute$$";


}
