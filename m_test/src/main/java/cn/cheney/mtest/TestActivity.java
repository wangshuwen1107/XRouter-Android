package cn.cheney.mtest;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.cheney.xrouter.annotation.XRoute;

@XRoute(path = "page1", module = "test")
public class TestActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtest_activity_1);
    }
}
