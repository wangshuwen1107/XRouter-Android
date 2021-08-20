package cn.cheney.mtest.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

import cn.cheney.mtest.R;
import cn.cheney.mtest.entity.Book;
import cn.cheney.xrouter.annotation.XMethod;
import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.core.XRouter;

@XRoute(module = "moduleA", path = "page/a")
public class ActivityA extends AppCompatActivity {
    @XParam()
    Book book;
    @XParam()
    List<String> infoList;
    @XParam()
    Map<String, String> infoMap;

    private TextView paramsText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        XRouter.getInstance().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("page/a");

        paramsText = findViewById(R.id.params_txt);
        paramsText.setText("Book:" + book + " \n" +
                "infoList:" + infoList + " \n" +
                "infoMap:" + infoMap + " \n");
    }

    @XMethod(name = "activityMethod")
    public static void activityMethod() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.exit_bottom);
    }
}
