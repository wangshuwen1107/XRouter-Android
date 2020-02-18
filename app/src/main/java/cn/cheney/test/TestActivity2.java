package cn.cheney.test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.annotation.XRoute;

@XRoute(path = "/test2", module = "home")
public class TestActivity2 extends AppCompatActivity {

    @XParam()
    public String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
    }
}
