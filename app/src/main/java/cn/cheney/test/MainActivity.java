package cn.cheney.test;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import cn.cheney.xrouter.core.RouteCallback;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.util.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
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

            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.name = "Kotlin";
                Book bookReturn = XRouter.<Book>method("home/getBookName")
                        .put("book", book)
                        .call();
                Logger.d("getSyncBookName bookReturn= " + bookReturn);
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.name = "Kotlin";
                XRouter.<Book>method("home/getAsyncBookName")
                        .put("book", book)
                        .call(new RouteCallback() {
                            @Override
                            public void onResult(Map<String, Object> result) {
                                Logger.d("getAsyncBookName bookReturn= " + result);
                            }
                        });
            }
        });

        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.name = "Kotlin";
                Book bookError = XRouter.<Book>method(null)
                        .put("book", book)
                        .call(new RouteCallback() {
                            @Override
                            public void onResult(Map<String, Object> result) {
                                Logger.d("getaaa bookReturn= " + result);
                            }
                        });
                Logger.d("getaaa bookError= " + bookError);
            }
        });
    }
}
