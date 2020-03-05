package cn.cheney.xrouter.core.invok;


import cn.cheney.xrouter.constant.RouteType;

public class MethodInvokable<R> extends Invokable<R> {

    private boolean async;

    public MethodInvokable(RouteType type,
                           Class<?> className,
                           String module,
                           String path,
                           boolean async) {
        this.type = type;
        this.path = path;
        this.module = module;
        this.clazz = className;
        this.async = async;
    }

    public boolean isAsync() {
        return async;
    }
}
