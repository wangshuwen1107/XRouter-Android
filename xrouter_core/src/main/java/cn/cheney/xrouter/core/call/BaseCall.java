package cn.cheney.xrouter.core.call;

import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.invok.Invokable;

public abstract class BaseCall<R, I extends Invokable<R>> {

    private String module;
    private String path;
    private Uri uri;
    Map<String, Object> paramsMap = new HashMap<>();
    I invokable;

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

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    public I getInvokable() {
        return invokable;
    }

    public void setInvokable(I invokable) {
        this.invokable = invokable;
    }

    private void parseUri(String uriStr) {
        if (TextUtils.isEmpty(uriStr)) {
            uri = Uri.EMPTY;
            return;
        }
        uri = Uri.parse(uriStr);
        if (TextUtils.isEmpty(uri.getScheme())) {
            uri = Uri.parse(XRouter.sScheme + "://" + uriStr);
        }

        this.module = uri.getHost();
        this.path = uri.getPath();
        if (!TextUtils.isEmpty(this.path)) {
            this.path = path.substring(1);
        }

        Set<String> paramsSet = uri.getQueryParameterNames();
        if (null == paramsSet || paramsSet.isEmpty()) {
            return;
        }
        for (String paramKey : paramsSet) {
            if (TextUtils.isEmpty(paramKey)) {
                continue;
            }
            String value = uri.getQueryParameter(paramKey);
            paramsMap.put(paramKey, value);
        }
    }


    public abstract R call();

}
