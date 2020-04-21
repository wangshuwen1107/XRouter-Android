package cn.cheney.xrouter.core.invok;


import java.util.ArrayList;
import java.util.Arrays;

import cn.cheney.xrouter.constant.RouteType;

public class MethodInvokable<R> extends Invokable<R> {

    private boolean async;

    public MethodInvokable(RouteType type,
                           Class<?> className,
                           String module,
                           String path,
                           boolean async,
                           ParamInfo... params) {
        this.type = type;
        this.path = path;
        this.module = module;
        this.clazz = className;
        this.async = async;
        this.params = params == null ? new ArrayList<ParamInfo>() : Arrays.asList(params);
    }

    public boolean isAsync() {
        return async;
    }
}
