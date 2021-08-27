package cn.cheney.mtest.interceptor;

import cn.cheney.xrouter.annotation.XInterceptor;
import cn.cheney.xrouter.core.interceptor.Chain;
import cn.cheney.xrouter.core.interceptor.RouterInterceptor;
import cn.cheney.xrouter.core.util.Logger;

@XInterceptor(modules = {"moduleA"}, priority = 101)
public class TestInterceptorA implements RouterInterceptor {

    @Override
    public Object intercept(Chain chain) {
        Logger.d("TestInterceptorA url=" + chain.call().getUri().toString());
        return chain.proceed(chain.call());
    }

}
