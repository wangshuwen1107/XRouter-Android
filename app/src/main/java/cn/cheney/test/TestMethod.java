package cn.cheney.test;

import android.util.Log;

import java.util.List;

import cn.cheney.xrouter.annotation.XMethod;
import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.annotation.XRoute;
import cn.cheney.xrouter.core.method.IMethod;

@XRoute(module = "home")
public class TestMethod implements IMethod {

    private static final String TAG = TestMethod.class.getSimpleName();

    @XMethod(name = "getBookName")
    public static Book getBookName(@XParam(name = "param1") String param1,
                                   @XParam(name = "param2") List<Book> param2) {
        Log.i(TAG, "getBookName  =" + param1);
        return new Book();
    }

}

