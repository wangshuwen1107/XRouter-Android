package cn.cheney.xrouter.core.call;

import cn.cheney.xrouter.core.RouteCallback;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.constant.GenerateFileConstant;
import cn.cheney.xrouter.core.invok.MethodInvokable;

public class MethodCall<R> extends BaseCall<R, MethodInvokable<R>> {

    public MethodCall(String uriStr) {
        super(uriStr);
    }

    @Override
    public R call() {
        if (!XRouter.getInstance().build(this)) {
            return null;
        }
        return invokable.invoke(paramsMap);
    }

    public R call(RouteCallback callback) {
        this.paramsMap.put(GenerateFileConstant.CALLBACK_KEY, callback);
        if (!XRouter.getInstance().build(this)) {
            callback.onResult(null);
            return null;
        }
        return invokable.invoke(paramsMap);
    }

    public MethodCall<R> put(String key, Object val) {
        this.paramsMap.put(key, val);
        return this;
    }

}
