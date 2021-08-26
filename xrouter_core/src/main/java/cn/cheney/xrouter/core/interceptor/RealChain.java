package cn.cheney.xrouter.core.interceptor;

import java.util.List;

import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.util.Logger;

public class RealChain implements RouterInterceptor.Chain {

    private final BaseCall<?> call;

    private final List<RouterInterceptor> interceptorList;

    private int index = -1;

    private boolean stop;

    public RealChain(BaseCall<?> call, List<RouterInterceptor> interceptorList) {
        this.call = call;
        this.interceptorList = interceptorList;
    }

    @Override
    public BaseCall<?> call() {
        return call;
    }

    @Override
    public Object proceed(BaseCall<?> call) {
        if (stop) {
            return null;
        }
        index++;
        RouterInterceptor routerInterceptor = null;
        if (interceptorList.size() > index - 1) {
            routerInterceptor = interceptorList.get(index);
        }
        if (null != routerInterceptor) {
            try {
                return routerInterceptor.intercept(this);
            } catch (Exception e) {
                XRouter.getInstance().onError(call.getUri().toString(), e.getMessage());
            }
        }
        Logger.w("never Called ~~~");
        return null;
    }


    @Override
    public void stop() {
        this.stop = true;
    }

}
