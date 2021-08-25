package cn.cheney.xrouter.core.interceptor;

import android.content.Context;

import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.exception.RouterException;

public interface RouterInterceptor {

    Object intercept(Context context, Chain chain) throws RouterException;

    interface Chain {

        BaseCall<?> call();

        Object proceed(Context context, BaseCall<?> call);

        void shutDown(String reason);
    }

}
