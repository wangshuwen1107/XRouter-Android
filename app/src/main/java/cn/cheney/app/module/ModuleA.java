package cn.cheney.app.module;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cheney.app.AlertUtil;
import cn.cheney.app.entity.Book;
import cn.cheney.xrouter.annotation.XMethod;
import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.core.XRouter;

@XRoute(module = "moduleA")
public class ModuleA {

    private static final String TAG = ModuleA.class.getSimpleName();

    @XMethod(name = "getBookName")
    public static Book getBookName(Context context) {
        Log.i(TAG, "getBookName context=" + context);
        Book book = new Book();
        book.name = "从入门到放弃";
        return book;
    }

    @XMethod(name = "getAsyncBookName")
    public static void getAsyncBookName(@XParam(name = XParam.RequestId) String requestId,
                                        @XParam(name = "book") Book book) {
        Log.i(TAG, "getBookName requestId =" + requestId + " book=" + book);
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
        XRouter.getInstance().invokeCallback(requestId, map);
    }


    @XMethod(name = "setBookInfo")
    public static List<String> setBookInfo(Context context, @XParam(name = "info") Map<String, Book> info) {
        for (Map.Entry<String, Book> entry : info.entrySet()) {
            Book book = entry.getValue();
            Log.i(TAG, "setBookInfo  KEY:" + entry.getKey() + "|VALUE:" + book);
        }
        AlertUtil.showAlert(context, "传参 =" + info);
        return new ArrayList<>();
    }

}

