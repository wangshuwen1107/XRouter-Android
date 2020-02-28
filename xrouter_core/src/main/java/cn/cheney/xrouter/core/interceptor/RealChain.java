package cn.cheney.xrouter.core.interceptor;

import java.util.List;

import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.invok.Invokable;

public class RealChain implements RouterInterceptor.Chain {

    private BaseCall call;

    private List<RouterInterceptor> interceptorList;

    private int index = -1;

    public RealChain(BaseCall call,
                     List<RouterInterceptor> interceptorList) {
        this.call = call;
        this.interceptorList = interceptorList;
    }

    @Override
    public BaseCall call() {
        return call;
    }

    @Override
    public Invokable proceed(BaseCall call) {
        index++;
        RouterInterceptor routerInterceptor = interceptorList.get(index);
        if (null != routerInterceptor) {
            return routerInterceptor.intercept(this);
        }
        return null;
    }


    @Override
    public void shutDown(String reason) {
        throw new RouterException(reason);
    }

}
