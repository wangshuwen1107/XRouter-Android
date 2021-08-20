package cn.cheney.app.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

import cn.cheney.app.entity.Book;
import cn.cheney.app.R;
import cn.cheney.xrouter.annotation.XMethod;
import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.core.XRouter;

@XRoute( module = "moduleA",path = "page/a")
public class ActivityA extends AppCompatActivity {

    public static final String TAG = ActivityA.class.getSimpleName();

    @XParam()
    Book book;

    @XParam()
    List<String> infoList;

    @XParam()
    Map<String, String> infoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        XRouter.getInstance().inject(this);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("moduleB Page");
        Log.i(TAG, "onCreate book=" + book);
        Log.i(TAG, "onCreate infoList=" + infoList);
        Log.i(TAG, "onCreate infoMap=" + infoMap);
    }

    @XMethod(name = "activityMethod")
    public static void activityMethod() {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_bottom, 0);
    }
}
