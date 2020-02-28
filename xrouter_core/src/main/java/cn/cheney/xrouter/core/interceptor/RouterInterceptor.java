package cn.cheney.xrouter.core.interceptor;

import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.invok.Invokable;

public interface RouterInterceptor {

    Invokable intercept(Chain chain) throws RouterException;

    interface Chain {

        BaseCall call();

        Invokable proceed(BaseCall call);

        void shutDown(String reason);
    }

}
