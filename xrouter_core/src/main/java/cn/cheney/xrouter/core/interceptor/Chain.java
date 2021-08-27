package cn.cheney.xrouter.core.interceptor;

import cn.cheney.xrouter.core.call.BaseCall;

public interface Chain {

    BaseCall<?> call();

    Object proceed();
}
