package cn.cheney.xrouter.core.interceptor;

import android.content.Context;

import cn.cheney.xrouter.constant.RouteType;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.call.PageCall;
import cn.cheney.xrouter.core.invok.ActivityInvoke;
import cn.cheney.xrouter.core.invok.Invokable;

public class BuildInvokeInterceptor implements RouterInterceptor {

    @Override
    public Object intercept(Context context, Chain chain) {
        BaseCall<?> call = chain.call();
        Invokable<?> invokable = XRouter.getInstance().getRouteModules().getInvokable(call.getModule(),
                call.getPath());
        if (null == invokable) {
            chain.shutDown("url=" + call.getUri().toString() + " Can`t Find Invokable");
            return null;
        }
        RouteType routeType = invokable.getType();
        if (null == routeType) {
            chain.shutDown("url=" + call.getUri().toString() + "Unknown routeType");
            return null;
        }
        switch (routeType) {
            case ACTIVITY:
                ActivityInvoke activityInvoke = (ActivityInvoke) invokable;
                PageCall pageCall = (PageCall) call;
                return activityInvoke.invoke(context, pageCall.getParamsMap(),
                        pageCall.getRequestCode(),
                        pageCall.getEnterAnim(),
                        pageCall.getExitAnim(),
                        pageCall.getAction());
            case METHOD:
                return invokable.invoke(call.getParamsMap());
            default:
                chain.shutDown("Url=" + call.getUri().toString()
                        + " not support route type=" + routeType.name());
                return null;
        }

    }
}
