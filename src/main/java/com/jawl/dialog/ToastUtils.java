package com.jawl.dialog;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast提示对话框工具类
 */

public class ToastUtils {

    private static Toast mToast;

    public static void showToast(Context context, String msg) {
        if (null == mToast) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
