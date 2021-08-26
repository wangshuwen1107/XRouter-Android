package cn.cheney.xrouter.core.interceptor;

import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.exception.RouterException;

public interface RouterInterceptor {

    Object intercept(Chain chain) throws RouterException;

    interface Chain {

        BaseCall<?> call();

        Object proceed(BaseCall<?> call);

        void stop();
    }

}
