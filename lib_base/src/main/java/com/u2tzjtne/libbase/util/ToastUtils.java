package com.u2tzjtne.libbase.util;

import android.annotation.SuppressLint;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.u2tzjtne.libbase.BaseApp;

/**
 * ToastUtils 利用单例模式，解决重命名toast重复弹出的问题
 * @author u2tzjtne
 */
public class ToastUtils {
    private static Toast mToast;

    @SuppressLint("ShowToast")
    public static void s(@NonNull String mString) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApp.getInstance(), mString, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(mString);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    @SuppressLint("ShowToast")
    public static void s(int messageID) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApp.getInstance(), messageID, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(messageID);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    @SuppressLint("ShowToast")
    public static void l(@NonNull String mString) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApp.getInstance(), mString, Toast.LENGTH_LONG);
        } else {
            mToast.setText(mString);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    @SuppressLint("ShowToast")
    public static void l(int messageID) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApp.getInstance(), messageID, Toast.LENGTH_LONG);
        } else {
            mToast.setText(messageID);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }
}
