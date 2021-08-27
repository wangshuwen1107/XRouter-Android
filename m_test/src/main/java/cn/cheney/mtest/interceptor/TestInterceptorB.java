package cn.cheney.mtest.interceptor;


import cn.cheney.xrouter.annotation.XInterceptor;
import cn.cheney.xrouter.core.interceptor.Chain;
import cn.cheney.xrouter.core.interceptor.RouterInterceptor;
import cn.cheney.xrouter.core.util.Logger;

@XInterceptor(paths = {"moduleA/page/a"}, priority = 100)
public class TestInterceptorB implements RouterInterceptor {

    @Override
    public Object intercept(Chain chain) {
        Logger.d("TestInterceptorB url=" + chain.call().getUri().toString());
        return chain.proceed();
    }
}
