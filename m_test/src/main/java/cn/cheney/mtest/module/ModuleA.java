package cn.cheney.mtest.module;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cn.cheney.mtest.entity.Book;
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

    @XMethod(name = "setBookInfo")
    public static void getAsyncBookName(@XParam(name = XParam.RequestId) String requestId,
                                        @XParam(name = "info") Map<String, Book> mapInfo) {
        Log.i(TAG, "setBookInfo requestId =" + requestId + " mapInfo=" + mapInfo);
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
        XRouter.getInstance().invokeCallback(requestId, map);
    }


}

