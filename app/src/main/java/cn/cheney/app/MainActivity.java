package cn.cheney.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.call.MethodCall;
import cn.cheney.xrouter.core.callback.RouteCallback;
import cn.cheney.xrouter.core.util.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> infoList = new ArrayList<>();
                infoList.add("value");
                Map<String, String> map = new HashMap<>();
                map.put("key", "value");
                Integer requestCode = XRouter.page("moduleA/page")
                        .put("infoList", infoList)
                        .put("infoMap", map)
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
                MethodCall<Book> bookMethodCall = XRouter.<Book>method(
                        "moduleA/getBookName");
                Book bookReturn = bookMethodCall.call();
                AlertUtil.showAlert(MainActivity.this, bookReturn.toString());
            }
        });


        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.name = "Kotlin";
                final MethodCall<Book> methodCall = XRouter.<Book>method("moduleA/getAsyncBookName")
                        .put("book", book);
                methodCall.call(new RouteCallback() {
                    @Override
                    public void onResult(Map<String, Object> result) {
                        AlertUtil.showAlert(MainActivity.this, result.toString());
                    }
                });
            }
        });
        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XRouter.<Void>method("moduleA/setBookInfo?info={\"key\":{\"name\":\"wang\"}}")
                        .call(MainActivity.this);
            }
        });

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.name = "Kotlin";
                Integer requestCode = XRouter.page("moduleD/page1")
                        //.action("cn.cheney.xrouter")
                        .anim(R.anim.enter_bottom, R.anim.exit_bottom)
                        .requestCode(1000)
                        .call();
                Logger.d("Route test Module page1 requestCode= " + requestCode);
            }
        });

        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book bookError = XRouter.<Book>method("sssss")
                        .call(new RouteCallback() {
                            @Override
                            public void onResult(Map<String, Object> result) {
                                AlertUtil.showAlert(MainActivity.this, "错误异步返回结果=" + result);
                            }
                        });
                AlertUtil.showAlert(MainActivity.this, "错误同步返回结果=" + bookError);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000) {
            if (null != data) {
                Uri uri = data.getData();
                Logger.d("uri=" + uri);
            }
        }
    }
}
