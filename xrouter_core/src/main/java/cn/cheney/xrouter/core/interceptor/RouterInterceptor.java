package cn.cheney.xrouter.core.interceptor;

import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.invok.Invokable;

public interface RouterInterceptor {

    Invokable intercept(Chain chain);

    interface Chain {

        BaseCall call();

        Invokable proceed(BaseCall call) throws RouterException;

        void shutDown(String reason);
    }

}
