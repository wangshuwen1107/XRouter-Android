package cn.cheney.xrouter.core.call;

import android.content.Context;

import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.core.RequestManager;
import cn.cheney.xrouter.core.callback.RouteCallback;
import cn.cheney.xrouter.core.XRouter;
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
        try {
            return invokable.invoke(paramsMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public R call(Context context) {
        return null;
    }

    public R call(RouteCallback callback) {
        return this.call(null, callback);
    }

    public R call(Context context, RouteCallback callback) {
        if (context == null) {
            context = XRouter.getInstance().getTopActivity();
        }
        String requestId = RequestManager.getInstance().generateRequestId();
        this.paramsMap.put(XParam.Context, context);
        this.paramsMap.put(XParam.RequestId, requestId);
        RequestManager.getInstance().addCallback(requestId, callback);
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


    public boolean isAsync() {
        if (null == invokable) {
            return false;
        }
        return invokable.isAsync();
    }
}
