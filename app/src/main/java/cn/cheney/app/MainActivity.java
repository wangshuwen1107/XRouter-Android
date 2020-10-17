package cn.cheney.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import cn.cheney.xrouter.core.callback.RouteCallback;
import cn.cheney.xrouter.core.XRouter;
import cn.cheney.xrouter.core.call.MethodCall;
import cn.cheney.xrouter.core.util.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer requestCode = XRouter.page("home/page?book={\"name\":\"Kotlin\"}")
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
                        "home/getBookName?book={\"name\":\"Kotlin\"}");
                Book bookReturn = bookMethodCall.call();
                Logger.d("getSyncBookName bookReturn= " + bookReturn
                        + " isAsync=" + bookMethodCall.isAsync());
            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.name = "Kotlin";
                final MethodCall<Book> methodCall = XRouter.<Book>method("home/getAsyncBookName")
                        .put("book", book);
                methodCall.call(new RouteCallback() {
                    @Override
                    public void onResult(Map<String, Object> result) {
                        Logger.d("getAsyncBookName bookReturn= "
                                + result + " isAsync=" + methodCall.isAsync());
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
                                Logger.d("Error  bookReturn= " + result);
                            }
                        });
                Logger.d(" bookError= " + bookError);
            }
        });
        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.name = "Kotlin";
                Integer requestCode = XRouter.page("test/page1")
                        .action("cn.cheney.xrouter")
                        .anim(R.anim.enter_bottom, R.anim.exit_bottom)
                        .requestCode(1000)
                        .call();

                Logger.d("Route test Module page1 requestCode= " + requestCode);
            }
        });
        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XRouter.method("home/setBookInfo?info={\"key\":{\"name\":\"wang\"}}")
                        .call();
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
