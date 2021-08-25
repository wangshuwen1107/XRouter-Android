package cn.cheney.app;

import android.app.Application;

import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.exception.RouterErrorHandler;
import cn.cheney.xrouter.core.util.Logger;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XRouter.init(this, "cheney");
        //url 拦截
//        XRouter.getInstance().addInterceptor(new RouterInterceptor() {
//            @Override
//            public Invokable<?> intercept(Chain chain) {
//                BaseCall<?> call = chain.call();
//                String urlStr = call.getUri().toString();
//                Logger.d("RouterInterceptor url=" + urlStr);
//                return chain.proceed(call);
//            }
//        });
        //全局错误监听
        XRouter.getInstance().setErrorHandler(new RouterErrorHandler() {
            @Override
            public void onError(String url, String errorMsg) {
                Logger.e("url=" + url + ", errorMsg=" + errorMsg);
            }
        });
    }

}
