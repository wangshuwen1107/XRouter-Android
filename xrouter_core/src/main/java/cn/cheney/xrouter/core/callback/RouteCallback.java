package cn.cheney.xrouter.core.callback;

import java.util.Map;

public abstract class RouteCallback {

    public void onResult(Map<String, Object> result){}

    public  void notFound(){}

    public void onError(int code, String message){}

}
