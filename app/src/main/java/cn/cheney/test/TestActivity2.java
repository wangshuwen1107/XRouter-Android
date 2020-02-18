package cn.cheney.test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.cheney.xrouter.annotation.XRoute;

@Route(path = "/app1/test2")
@XRoute(path = "/test2", module = "home")
public class TestActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
    }
}
