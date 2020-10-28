package cn.cheney.mtest;

import cn.cheney.xrouter.annotation.XMethod;
import cn.cheney.xrouter.annotation.XRoute;

@XRoute(module = "moduleC")
public class TestModule {

    @XMethod(name = "getBookName")
    public static String getBookName() {
        return "Java";
    }
}
