package cn.cheney.test;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.util.Logger;

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

                Integer requestCode = XRouter.page("home/test1")
                        .put("testParam", book)
                        .action("cn.cheney.xrouter")
                        .anim(R.anim.enter_bottom, R.anim.exit_bottom)
                        .requestCode(1000)
                        .call();

                Logger.d("Route Page requestCode= " + requestCode);

                Book bookReturn = XRouter.<Book>method("home/getBookName")
                        .put("book", book)
                        .call();
                Logger.d("Route Method bookReturn= " + bookReturn);
            }
        });
    }
}
