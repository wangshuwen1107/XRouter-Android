package cn.cheney.test;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;

import cn.cheney.xrouter.core.XRouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/test1").navigation();

                Book book = new Book();
                book.name = "Kotlin";
                XRouter.build("home/test1")
                        .put("testParam", book)
                        .start();
            }
        });
    }
}
