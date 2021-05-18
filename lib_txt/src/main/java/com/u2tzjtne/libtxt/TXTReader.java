package com.u2tzjtne.libtxt;

import android.content.Context;

import com.u2tzjtne.libbase.BaseApp;

/**
 * @author u2tzjtne@gmail.com
 * @date 2020/4/26
 */
public class TXTReader {
    private static TXTReader singleton = null;
    private Context context;
    public TXTReader(Context context) {
        this.context = context;
    }

    public static TXTReader get() {

        if (singleton == null) {
            synchronized (TXTReader.class) {
                if (singleton == null) {
                    if (BaseApp.getContext() == null) {
                        throw new IllegalStateException("-> context == null");
                    }
                    singleton = new TXTReader(BaseApp.getContext());
                }
            }
        }
        return singleton;
    }

    public void open(){

    }
}
