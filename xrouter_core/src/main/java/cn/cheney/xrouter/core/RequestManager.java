package cn.cheney.xrouter.core;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.cheney.xrouter.core.callback.RouteCallback;

public class RequestManager {


    private static volatile RequestManager instance;

    private Map<String, RouteCallback> callbackMap;

    private RequestManager() {
        callbackMap = new HashMap<>();
    }

    public static RequestManager getInstance() {
        if (instance == null) {
            synchronized (XRouter.class) {
                if (instance == null) {
                    instance = new RequestManager();
                }
            }
            return instance;
        }
        return instance;
    }


    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    public void addCallback(String requestId, RouteCallback callback) {
        if (null != callback && !TextUtils.isEmpty(requestId)) {
            callbackMap.put(requestId, callback);
        }
    }

    public void invokeCallback(String requestId, Map<String, Object> result) {
        RouteCallback routeCallback = callbackMap.remove(requestId);
        if (null == routeCallback) {
            return;
        }
        routeCallback.onResult(result);
    }

}
