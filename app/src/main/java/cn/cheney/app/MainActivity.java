package cn.cheney.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cheney.mtest.AlertUtil;
import cn.cheney.mtest.entity.Book;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.callback.RouteCallback;

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
                    .requestCode(2000)
                    .call(this, new RouteCallback() {
                        @Override
                        public void onResult(Map<String, Object> result) {
                            AlertUtil.showAlert(MainActivity.this, JSON.toJSONString(result));
                        }

                        @Override
                        public void onError(int code, String message) {

                        }

                        @Override
                        public void notFound() {
                        }
                    });
        });

        //路由执行同步方法
        findViewById(R.id.btn2).setOnClickListener(v -> {
            Book book = new Book();
            book.name = "Kotlin";
            List<Book> bookList = new ArrayList<>();
            bookList.add(book);
            Boolean setSuccess = XRouter.<Boolean>method("moduleA/setBookInfo?bookList="
                    + JSON.toJSONString(bookList))
                    .call();
            AlertUtil.showAlert(MainActivity.this, "" + setSuccess);
        });

        //路由执行异步方法+测试uri encode逻辑
        findViewById(R.id.btn3).setOnClickListener(v -> {
            Book book = new Book();
            book.name = "Kotlin";
            //测试urlEncode参数
            XRouter.<Book>method("moduleA/asyncSetBookName?info=%7B%22key%22%3A%20%22%26info2%3D123%22%7D")
                    .put("book", book).call(new RouteCallback() {
                @Override
                public void onResult(Map<String, Object> result) {
                    String content = result == null ? "null" : result.toString();
                    AlertUtil.showAlert(MainActivity.this, content);
                }

                @Override
                public void onError(int code, String message) {
                    String content = "code=" + code + ";message=" + message;
                    AlertUtil.showAlert(MainActivity.this, content);
                }
            });
        });

        //未找到路由测试
        findViewById(R.id.btn4).setOnClickListener(v -> {
            Book bookError = XRouter.<Book>method("sssss")
                    .call(new RouteCallback() {
                        @Override
                        public void onResult(Map<String, Object> result) {
                            super.onResult(result);
                        }

                        @Override
                        public void notFound() {
                            AlertUtil.showAlert(MainActivity.this, "没找到路由");
                        }

                        @Override
                        public void onError(int code, String message) {
                            super.onError(code, message);
                        }
                    });
        });
    }

}
