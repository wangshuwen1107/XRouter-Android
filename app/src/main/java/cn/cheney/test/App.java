package cn.cheney.test;

import android.app.Application;

import cn.cheney.xrouter.core.XRouter;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XRouter.init(this, "dc");
    }
}
