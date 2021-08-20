package cn.cheney.app;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class AlertUtil {

    public static void showAlert(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle("Result")
                .setMessage(msg)
                .create()
                .show();
    }
}
