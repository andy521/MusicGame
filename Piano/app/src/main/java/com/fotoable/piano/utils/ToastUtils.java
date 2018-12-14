package com.fotoable.piano.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by fotoable on 2017/7/7.
 */

public class ToastUtils {

    private static Toast mToast;

    public static void showToast(Context context, String str) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
        mToast.show();
    }

    public static void showToast(Context context, int strRes) {
        if (mToast == null) {
            mToast = Toast.makeText(context, strRes, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(strRes);
        }
        mToast.show();
    }
}

