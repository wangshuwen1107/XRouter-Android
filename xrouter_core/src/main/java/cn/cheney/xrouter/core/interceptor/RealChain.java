package cn.cheney.xrouter.core.interceptor;

import java.util.List;

import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.util.Logger;

public class RealChain implements Chain {

    private final BaseCall<?> call;

    private final List<RouterInterceptor> interceptorList;

    private int index = -1;

    private boolean stop = false;

    public RealChain(BaseCall<?> call, List<RouterInterceptor> interceptorList) {
        this.call = call;
        this.interceptorList = interceptorList;
    }

    @Override
    public BaseCall<?> call() {
        return call;
    }

    @Override
    public Object proceed() {
        if (stop) {
            //可以再次做拦截记录
            Logger.d("uri=" + call.getUri().toString() + " has been intercept");
            throw new RouterException(RouterException.HAS_INTERCEPT);
        }
        index++;
        RouterInterceptor routerInterceptor = null;
        if (index <= interceptorList.size() - 1) {
            routerInterceptor = interceptorList.get(index);
        }
        if (null != routerInterceptor) {
            return routerInterceptor.intercept(this);
        }
        Logger.w("never called ~~~");
        return null;
    }

    @Override
    public void stop() {
        stop = true;
        proceed();
    }

}
