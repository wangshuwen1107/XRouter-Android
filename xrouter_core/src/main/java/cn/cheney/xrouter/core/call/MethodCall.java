package cn.cheney.xrouter.core.call;

import android.content.Context;

import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.invok.MethodInvokable;

public class MethodCall<R> extends BaseCall<MethodInvokable<R>, R> {

    public MethodCall(String uriStr) {
        super(uriStr);
    }

    @Override
    public R call(Context context) {
        XRouter.getInstance().setInvokable(this);
        if (null == invokable) {
            return null;
        }
        return invokable.invoke(context, paramsMap);
    }

    public MethodCall<R> put(String key, Object val) {
        this.paramsMap.put(key, val);
        return this;
    }

}
