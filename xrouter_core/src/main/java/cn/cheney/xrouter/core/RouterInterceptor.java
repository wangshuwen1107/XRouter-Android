package cn.cheney.xrouter.core;

import android.content.Intent;

import java.io.IOException;

public interface RouterInterceptor {

    Intent intercept(Chain chain);

    interface Chain {

        Intent orginalIntent();

        Intent proceed(Intent request) throws IOException;
    }

}
