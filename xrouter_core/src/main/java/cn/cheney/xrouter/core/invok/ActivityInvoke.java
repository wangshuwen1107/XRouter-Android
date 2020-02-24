package cn.cheney.xrouter.core.invok;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Map;

import cn.cheney.xrouter.core.constant.RouteType;
import cn.cheney.xrouter.core.exception.RouterException;

public class ActivityInvoke extends Invokable<Integer> {


    public ActivityInvoke(RouteType type,
                          Class<?> className,
                          String module,
                          String path) {
        this.type = type;
        this.path = path;
        this.module = module;
        this.clazz = className;
    }

    public Integer invoke(@Nullable Context context,
                          @NonNull Map<String, Object> params,
                          int requestCode, int enterAnim, int exitAnim, String action) {
        if (null == context) {
            return -1;
        }
        Intent intent = new Intent(context, clazz);
        fillIntent(intent, params);
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return requestCode >= 0 ? requestCode : 1;
        }
        Activity activity = ((Activity) context);
        if (requestCode >= 0) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivity(intent);
        }
        if (enterAnim >= 0 || exitAnim >= 0) {
            activity.overridePendingTransition(enterAnim >= 0 ? enterAnim : -1,
                    enterAnim >= 0 ? enterAnim : -1);
        }
        return requestCode >= 0 ? requestCode : 1;
    }

    private static Intent fillIntent(Intent intent, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            final Object value = entry.getValue();
            if (value instanceof String) {
                intent.putExtra(entry.getKey(), (String) value);
            } else if (value instanceof Integer) {
                intent.putExtra(entry.getKey(), (Integer) value);
            } else if (value instanceof Boolean) {
                intent.putExtra(entry.getKey(), (Boolean) value);
            } else if (value instanceof Bundle) {
                intent.putExtra(entry.getKey(), (Bundle) value);
            } else if (value instanceof Parcelable) {
                intent.putExtra(entry.getKey(), (Parcelable) value);
            } else if (value instanceof CharSequence) {
                intent.putExtra(entry.getKey(), (CharSequence) value);
            } else if (value instanceof Byte) {
                intent.putExtra(entry.getKey(), (Byte) value);
            } else if (value instanceof Character) {
                intent.putExtra(entry.getKey(), (Character) value);
            } else if (value instanceof Double) {
                intent.putExtra(entry.getKey(), (Double) value);
            } else if (value instanceof Float) {
                intent.putExtra(entry.getKey(), (Float) value);
            } else if (value instanceof String[]) {
                intent.putExtra(entry.getKey(), (String[]) value);
            } else if (value instanceof CharSequence[]) {
                intent.putExtra(entry.getKey(), (CharSequence[]) value);
            } else if (value instanceof boolean[]) {
                intent.putExtra(entry.getKey(), (boolean[]) value);
            } else if (value instanceof byte[]) {
                intent.putExtra(entry.getKey(), (byte[]) value);
            } else if (value instanceof char[]) {
                intent.putExtra(entry.getKey(), (char[]) value);
            } else if (value instanceof double[]) {
                intent.putExtra(entry.getKey(), (double[]) value);
            } else if (value instanceof float[]) {
                intent.putExtra(entry.getKey(), (float[]) value);
            } else if (value instanceof int[]) {
                intent.putExtra(entry.getKey(), (int[]) value);
            } else if (value instanceof Parcelable[]) {
                intent.putExtra(entry.getKey(), (Parcelable[]) value);
            } else if (value instanceof Serializable) {
                intent.putExtra(entry.getKey(), (Serializable) value);
            } else if (value != null) {
                throw new RouterException("NOT SUPPORT TYPE=" + value);
            }
        }
        return intent;
    }

}
