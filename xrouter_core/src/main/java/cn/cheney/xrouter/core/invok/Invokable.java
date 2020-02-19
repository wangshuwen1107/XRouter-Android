package cn.cheney.xrouter.core.invok;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;


public abstract class Invokable<R> {


    public abstract R invoke(@Nullable Context context, @NonNull Map<String, Object> params);

}

