package cn.cheney.xrouter.core;

import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RouteIntent {

    private String module;
    private String path;
    private Map<String, Object> paramsMap = new HashMap<>();
    private Uri uri;

    RouteIntent(String uriStr) {
        parseUri(uriStr);
    }

    public String getModule() {
        return module;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    private void parseUri(String uriStr) {
        if (TextUtils.isEmpty(uriStr)) {
            return;
        }
        uri = Uri.parse(uriStr);
        if (TextUtils.isEmpty(uri.getScheme())) {
            uri = Uri.parse(XRouter.sScheme + "://" + uriStr);
        }

        this.module = uri.getHost();
        this.path = uri.getPath();

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


    public static Builder newBuilder(String uriStr) {
        return new Builder(uriStr);
    }

    public static class Builder {

        private RouteIntent mRouteIntent;

        private Builder(String uriStr) {
            this.mRouteIntent = new RouteIntent(uriStr);
        }

        public Builder put(String key, Object val) {
            this.mRouteIntent.paramsMap.put(key, val);
            return this;
        }

        public void start() {
            XRouter.getInstance().start(this.mRouteIntent);
        }
    }

}
