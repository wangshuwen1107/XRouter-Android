package cn.cheney.xrouter.core.invok;

import cn.cheney.xrouter.core.constant.RouteType;

public class MethodInvokable extends Invokable {


    public MethodInvokable(RouteType type,
                           Class<?> className,
                           String module,
                           String path) {
        this.type = type;
        this.path = path;
        this.module = module;
        this.clazz = className;
    }


}