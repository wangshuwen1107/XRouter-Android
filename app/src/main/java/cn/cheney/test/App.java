package cn.cheney.test;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

import cn.cheney.xrouter.core.XRouter;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);
        XRouter.init(this, "");
    }
}
