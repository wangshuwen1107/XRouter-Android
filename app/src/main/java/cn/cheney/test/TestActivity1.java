package cn.cheney.test;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.core.XRouter;

@XRoute(path = "/test1", module = "home")
public class TestActivity1 extends AppCompatActivity {

    public static final String TAG = TestActivity1.class.getSimpleName();

    @XParam(name = "testParam")
    Book book;

    String gogog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        XRouter.getInstance().inject(this);
        Log.i(TAG, "name =" + book + " action=" + getIntent().getAction());
    }

}
