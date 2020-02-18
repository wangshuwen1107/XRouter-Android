package cn.cheney.xrouter.core.invok;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import cn.cheney.xrouter.core.entity.XRouteMeta;


public abstract class Invokable<R> {

    protected XRouteMeta routeMeta;

    abstract R invoke(@Nullable Context context, @NonNull Map<String, Object> params);

}

