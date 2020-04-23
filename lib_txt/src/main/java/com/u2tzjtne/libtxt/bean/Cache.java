package com.u2tzjtne.libtxt.bean;

import java.lang.ref.WeakReference;

/**
 *
 * @author Administrator
 * @date 2016/8/15 0015
 */
public class Cache {
    private long size;
    private WeakReference<char[]> data;

    public WeakReference<char[]> getData() {
        return data;
    }

    public void setData(WeakReference<char[]> data) {
        this.data = data;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
