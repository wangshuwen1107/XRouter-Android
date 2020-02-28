package cn.cheney.xrouter.core.interceptor;

import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.constant.RouteType;
import cn.cheney.xrouter.core.invok.Invokable;

public class BuildInvokeInterceptor implements RouterInterceptor {

    @Override
    public Invokable intercept(Chain chain) {
        BaseCall call = chain.call();

        Invokable invokable = XRouter.getInstance().getRouteModules().getRouteMeta(call.getModule(),
                call.getPath());
        if (null == invokable) {
            chain.shutDown("url=" + call.getUri().toString() + " Can`t Find Invokable");
            return null;
        }
        RouteType routeType = invokable.getType();
        if (null == routeType) {
            chain.shutDown("Url=" + call.getUri().toString() + "  Unknown routeType");
            return null;
        }
        switch (routeType) {
            case ACTIVITY:
            case METHOD:
                return invokable;
            default:
                chain.shutDown("Url=" + call.getUri().toString()
                        + " not support route type=" + routeType.name());
        }
        return null;
    }
}
