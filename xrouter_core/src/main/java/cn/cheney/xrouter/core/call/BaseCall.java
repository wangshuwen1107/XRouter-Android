package cn.cheney.xrouter.core.call;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.callback.RouteCallback;

public abstract class BaseCall<R> {

    private String module;
    private String path;
    private Uri uri;
    protected Context context;
    Map<String, Object> paramsMap = new HashMap<>();

    BaseCall(String uriStr) {
        parseUri(uriStr);
    }

    public String getModule() {
        return module;
    }

    public String getPath() {
        return path;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        if (null == uri) {
            return;
        }
        this.uri = uri;
        rebuild();
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    private void parseUri(String uriStr) {
        if (TextUtils.isEmpty(uriStr)) {
            uri = Uri.EMPTY;
            return;
        }
        if (!uriStr.contains("://")) {
            uriStr = XRouter.sScheme + "://" + uriStr;
        }
        uri = Uri.parse(uriStr);
        rebuild();
    }

    public Context getContext() {
        return context;
    }

    private void rebuild() {
        this.module = uri.getHost();
        this.path = uri.getPath();
        if (!TextUtils.isEmpty(this.path)) {
            this.path = path.substring(1);
        }
        String query = uri.getQuery();
        if (TextUtils.isEmpty(query)) {
            return;
        }
        Set<String> parameterNames = uri.getQueryParameterNames();
        if (parameterNames.isEmpty()) {
            return;
        }
        for (String paramName : parameterNames) {
            String paramValue = uri.getQueryParameter(paramName);
            paramsMap.put(paramName, XRouter.getInstance().parse(this, paramName, paramValue));
        }
    }

    public R call() {
        return call(XRouter.getInstance().getTopActivity(), null);
    }

    public R call(Context context) {
        return call(context, null);
    }

    public R call(RouteCallback callback) {
        return call(null, callback);
    }

    public abstract R call(Context context, RouteCallback callback);

}
