package cn.cheney.mtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.util.Logger;

@XRoute(path = "page1", module = "test")
public class TestActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtest_activity_1);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object bookReturn = XRouter.method("home/getBookName2")
                        .call();
                Logger.d("getSyncBookName bookReturn= " + bookReturn);
            }
        });
    }
}
