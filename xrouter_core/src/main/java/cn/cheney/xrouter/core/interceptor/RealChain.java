package cn.cheney.xrouter.core.interceptor;

import android.content.Context;

import java.util.List;

import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.util.Logger;

public class RealChain implements RouterInterceptor.Chain {

    private final BaseCall<?> call;

    private final List<RouterInterceptor> interceptorList;

    private int index = -1;

    public RealChain(BaseCall<?> call, List<RouterInterceptor> interceptorList) {
        this.call = call;
        this.interceptorList = interceptorList;
    }

    @Override
    public BaseCall<?> call() {
        return call;
    }

    @Override
    public Object proceed(Context context, BaseCall<?> call) {
        index++;
        RouterInterceptor routerInterceptor = null;
        if (interceptorList.size() > index - 1) {
            routerInterceptor = interceptorList.get(index);
        }
        if (null != routerInterceptor) {
            try {
                return routerInterceptor.intercept(context, this);
            } catch (RouterException e) {
                XRouter.getInstance().onError(call.getUri().toString(), e.getMessage());
            }
        }
        Logger.w("never Called ~~~");
        return null;
    }


    @Override
    public void shutDown(String reason) {
        throw new RouterException(reason);
    }

}
