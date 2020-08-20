package cn.cheney.app;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import cn.cheney.xrouter.annotation.XMethod;
import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.core.XRouter;

@XRoute(path = "page", module = "home")
public class HomeActivity extends AppCompatActivity {

    public static final String TAG = HomeActivity.class.getSimpleName();

    @XParam()
    Book book;

    @XParam()
    List<String> infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        XRouter.getInstance().inject(this);

        Log.i(TAG, "onCreate book=" + book);
    }

    @XMethod(name = "activityMethod")
    public static void activityMethod(){

    }


}
