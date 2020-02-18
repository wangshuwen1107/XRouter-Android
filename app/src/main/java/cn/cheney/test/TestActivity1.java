package cn.cheney.test;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.cheney.xrouter.annotation.Param;
import cn.cheney.xrouter.core.XRouter;

@Route(path = "/app1/test1")
//@XRoute(path = "/test1", module = "home")
public class TestActivity1 extends AppCompatActivity {


    //@Autowired
    @Param(name = "testParam")
    public Book book;
    @Param(name = "")
    public String gogog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        XRouter.getInstance().inject(this);
        Log.i("test", "自动注入 name =" + book);
    }
}
