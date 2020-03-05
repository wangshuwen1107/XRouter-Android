package cn.cheney.app;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cn.cheney.xrouter.annotation.XMethod;
import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.core.RouteCallback;
import cn.cheney.xrouter.core.method.IMethod;

@XRoute(module = "home")
public class HomeMethod implements IMethod {

    private static final String TAG = HomeMethod.class.getSimpleName();

    @XMethod(name = "getBookName")
    public static Map<String,String> getBookName(@XParam(name = "book") Book book) {
        Log.i(TAG, "getBookName  =" + book);
        Map<String, String> map = new HashMap<>();
        return map;
    }


    @XMethod(name = "getAsyncBookName")
    public static void getAsyncBookName(@XParam(name = "book") Book book, RouteCallback callback) {
        Log.i(TAG, "getBookName  =" + book);
        HashMap<String, Object> map = new HashMap<>();
        map.put("book", book);
        callback.onResult(map);
    }

    @XMethod(name = "getBookName2")
    public static Book getBookName() {
        Log.i(TAG, "getBookName2 is called   ");
        Book book = new Book();
        book.name = "php";
        return book;
    }

}

