package cn.cheney.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cheney.mtest.AlertUtil;
import cn.cheney.mtest.entity.Book;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.call.MethodCall;
import cn.cheney.xrouter.core.callback.RouteCallback;
import cn.cheney.xrouter.core.util.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //路由跳转界面
        findViewById(R.id.btn1).setOnClickListener(v -> {
            List<String> infoList = new ArrayList<>();
            infoList.add("stringValue");
            Map<String, String> map = new HashMap<>();
            map.put("key", "stringValue");
            XRouter.page("moduleA/page/a")
                    .put("infoList", infoList)
                    .put("infoMap", map)
                    .put("book", new Book("Java"))
                    .action("cn.cheney.xrouter")
                    .anim(R.anim.enter_bottom, R.anim.no_anim)
                    .call();
        });

        //路由执行同步方法
        findViewById(R.id.btn2).setOnClickListener(v -> {
            Book book = new Book();
            book.name = "Kotlin";
            MethodCall<Book> bookMethodCall = XRouter.<Book>method(
                    "moduleA/getBookName");
            Book bookReturn = bookMethodCall.call();
            AlertUtil.showAlert(MainActivity.this, bookReturn.toString());
        });

        //路由执行异步方法
        findViewById(R.id.btn3).setOnClickListener(v -> {
            Book book = new Book();
            book.name = "Kotlin";
            XRouter.<Book>method("moduleA/setBookInfo?info={\"key\":{\"name\":\"wang\"}}")
                    .put("book", book).call(result ->
                    AlertUtil.showAlert(MainActivity.this, result.toString()));
        });

        findViewById(R.id.btn4).setOnClickListener(v -> {
            Book bookError = XRouter.<Book>method("sssss")
                    .call(new RouteCallback() {
                        @Override
                        public void onResult(Map<String, Object> result) {
                            AlertUtil.showAlert(MainActivity.this, "错误异步返回结果=" + result);
                        }
                    });
            AlertUtil.showAlert(MainActivity.this, "错误同步返回结果=" + bookError);
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
