package cn.cheney.mtest.module;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cheney.mtest.entity.Book;
import cn.cheney.xrouter.annotation.XMethod;
import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.core.XRouter;

@XRoute(module = "moduleB")
public class ModuleB {

    private static final String TAG = ModuleB.class.getSimpleName();

    @XMethod(name = "asyncSetBookName")
    public static void asyncSetBookName(@XParam(name = XParam.RequestId) String requestId,
                                        @XParam(name = "info") Map<String, String> mapInfo) {
        Log.i(TAG, "setBookInfo requestId =" + requestId + " mapInfo=" + mapInfo);
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
        XRouter.getInstance().invokeCallback(requestId, map);
    }

    @XMethod(name = "setBookInfo")
    public static Boolean setBookList(Context context, List<Book> bookList, String strValue) {
        Log.i(TAG, "setBookList bookList=" + JSON.toJSONString(bookList));
        Log.i(TAG, "setBookList strValue=" + strValue);
        return true;
    }


}

