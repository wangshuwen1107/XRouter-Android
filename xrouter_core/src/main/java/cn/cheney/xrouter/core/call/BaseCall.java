package cn.cheney.xrouter.core.call;

import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (!uriStr.contains("://")) {
            uriStr = XRouter.sScheme + "://" + uriStr;
        }
        uri = Uri.parse(uriStr);
        rebuild();
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
        List<String> nameAndValueList = toNamesAndValues(query);
        if (nameAndValueList.isEmpty()) {
            return;
        }
        for (int i = 0; i < nameAndValueList.size(); i = i + 2) {
            String key = nameAndValueList.get(i);
            String value = nameAndValueList.get(i + 1);
            paramsMap.put(key, XRouter.getInstance().parse(this, key, value));
        }
    }

    public static List<String> toNamesAndValues(String encodedQuery) {
        List<String> result = new ArrayList<>();
        if (encodedQuery.length() == 0) return result;
        for (int pos = 0; pos <= encodedQuery.length(); ) {
            int ampersandOffset = encodedQuery.indexOf('&', pos);
            if (ampersandOffset == -1) ampersandOffset = encodedQuery.length();

            int equalsOffset = encodedQuery.indexOf('=', pos);
            if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
                result.add(encodedQuery.substring(pos, ampersandOffset));
                result.add(null); // No value for this name.
            } else {
                result.add(encodedQuery.substring(pos, equalsOffset));
                result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
            }
            pos = ampersandOffset + 1;
        }
        return result;
    }

    public abstract R call();

}
