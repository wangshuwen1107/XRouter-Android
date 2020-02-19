package cn.cheney.xrouter.core;

import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RouteIntent {

    private Map<String, Object> paramsMap = new HashMap<>();
    private String module;
    private String path;
    private Uri uri;
    private String action;
    private int enterAnim = -1;
    private int exitAnim = -1;

    RouteIntent(String uriStr) {
        parseUri(uriStr);
    }

    public String getModule() {
        return module;
    }

    public String getPath() {
        return path;
    }

    public String getAction() {
        return action;
    }

    public Uri getUri() {
        return uri;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public int getExitAnim() {
        return exitAnim;
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

        public Builder action(String action) {
            this.mRouteIntent.action = action;
            return this;
        }

        public Builder anim(int enterAnim, int exitAnim) {
            mRouteIntent.enterAnim = enterAnim;
            mRouteIntent.exitAnim = exitAnim;
            return this;
        }

        public void start() {
            start(-1);
        }

        public void start(int requestCode) {
            XRouter.getInstance().start(this.mRouteIntent, requestCode);
        }


    }

}
