package cn.cheney.xrouter.core.call;

import android.content.Context;

import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.core.RequestManager;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.callback.RouteCallback;

public class MethodCall<R> extends BaseCall<R> {

    public MethodCall(String uriStr) {
        super(uriStr);
    }

    @Override
    public R call() {
        return this.call(null, null);
    }

    @Override
    public R call(Context context) {
        return this.call(context, null);
    }

    public R call(RouteCallback callback) {
        return this.call(null, callback);
    }

    public R call(Context context, RouteCallback callback) {
        if (context == null) {
            context = XRouter.getInstance().getTopActivity();
        }
        this.paramsMap.put(XParam.Context, context);
        if (null != callback) {
            String requestId = RequestManager.getInstance().generateRequestId();
            this.paramsMap.put(XParam.RequestId, requestId);
            RequestManager.getInstance().addCallback(requestId, callback);
        }
        R result = (R) XRouter.getInstance().proceed(context, this);
        if (null == result) {
            if (null != callback) {
                callback.onResult(null);
            }
        }
        return result;
    }

    public MethodCall<R> put(String key, Object val) {
        this.paramsMap.put(key, val);
        return this;
    }

}
