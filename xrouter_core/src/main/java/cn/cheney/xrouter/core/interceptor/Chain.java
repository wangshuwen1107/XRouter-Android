package cn.cheney.xrouter.core.interceptor;

import cn.cheney.xrouter.core.call.BaseCall;

public interface Chain {

    BaseCall<?> call();

    /**
     * @throws  cn.cheney.xrouter.core.exception.RouterException
     * @return value
     */
    Object proceed();
}
