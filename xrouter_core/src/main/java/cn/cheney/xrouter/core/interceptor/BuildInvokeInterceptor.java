package cn.cheney.xrouter.core.interceptor;

import cn.cheney.xrouter.constant.RouteType;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.call.PageCall;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.invok.ActivityInvoke;
import cn.cheney.xrouter.core.invok.Invokable;

public class BuildInvokeInterceptor implements RouterInterceptor {

    @Override
    public Object intercept(Chain chain) {
        BaseCall<?> call = chain.call();
        Invokable<?> invokable = XRouter.getInstance().getRouterMap()
                .get(call.getModule() + "/" + call.getPath());
        if (null == invokable) {
            throw new RouterException(RouterException.NOT_FOUND);
        }
        RouteType routeType = invokable.getType();
        if (null == routeType) {
            throw new RouterException(RouterException.UN_KNOWN);
        }
        invokable.setUri(call.getUri());
        try {
            switch (routeType) {
                case ACTIVITY:
                    ActivityInvoke activityInvoke = (ActivityInvoke) invokable;
                    PageCall pageCall = (PageCall) call;
                    return activityInvoke.invoke(pageCall.getContext(), pageCall.getParamsMap(),
                            pageCall.getRequestCode(),
                            pageCall.getEnterAnim(),
                            pageCall.getExitAnim(),
                            pageCall.getAction());
                case METHOD:
                    return invokable.invoke(call.getParamsMap());
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RouterException(RouterException.INVOKE_ERROR);
        }

    }
}
