package com.u2tzjtne.libbase;

import android.app.Application;
import android.content.Context;

/**
 * @author u2tzjtne
 * @date 2020/4/24
 * Email u2tzjtne@gmail.com
 */
public class BaseApp extends Application {
    private static BaseApp instance;
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
            context = getApplicationContext();
        }
    }
    public static BaseApp getInstance() {
        return instance;
    }

    public static Context getContext() {
        return context;
    }
}
