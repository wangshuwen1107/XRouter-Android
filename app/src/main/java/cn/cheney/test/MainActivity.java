package cn.cheney.test;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cn.cheney.xrouter.core.XRouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.name = "Kotlin";

               XRouter.build("home/test1")
                        .put("testParam", book)
                        .action("cn.cheney.xrouter")
                        .anim(R.anim.enter_bottom, R.anim.exit_bottom)
                        .start(1000);


            }
        });
    }
}
