package cn.cheney.test;

import android.app.Application;

import cn.cheney.xrouter.core.exception.RouterErrorHandler;
import cn.cheney.xrouter.core.interceptor.RouterInterceptor;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.invok.Invokable;
import cn.cheney.xrouter.core.util.Logger;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XRouter.init(this, "dc");
        XRouter.getInstance().addInterceptor(new RouterInterceptor() {
            @Override
            public Invokable intercept(Chain chain) {
                BaseCall call = chain.call();
                String urlStr = call.getUri().toString();
                Logger.d(" RouterInterceptor urlStr=" + urlStr);
                return chain.proceed(call);
            }
        });
        XRouter.getInstance().setErroHandler(new RouterErrorHandler() {
            @Override
            public void onError(String url, String errorMsg) {
                Logger.e("Url:" + url + " errorMsg:" + errorMsg);
            }
        });
    }
}
