package com.u2tzjtne.libbase.util;

import android.util.Log;


import com.u2tzjtne.libbase.base.BuildConfig;

import java.util.Formatter;

/**
 * 1.支持自动定位当前类名
 * 2.支持大于4K的长字符
 *
 * @author u2tzjtne@gmail.com
 * @date 2020/04/27
 */
public class LogUtils {

    private static final int MAX_LEN = 4000;
    private static final int V = 0x01;
    private static final int D = 0x02;
    private static final int I = 0x04;
    private static final int W = 0x08;
    private static final int E = 0x10;
    private static boolean mDebuggable = BuildConfig.DEBUG;

    /**
     * 是否允许输出log
     *
     * @param mDebuggable
     */
    public static void setDebuggable(boolean mDebuggable) {
        LogUtils.mDebuggable = mDebuggable;
    }

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void v(String msg) {
        buildMessage(V, msg);
    }

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void d(String msg) {
        buildMessage(D, msg);
    }

    /**
     * 以级别为 i 的形式输出LOG
     */
    public static void i(String msg) {
        buildMessage(I, msg);
    }

    /**
     * 以级别为 w 的形式输出LOG
     */
    public static void w(String msg) {
        buildMessage(W, msg);
    }

    /**
     * 以级别为 e 的形式输出LOG
     */
    public static void e(String msg) {
        buildMessage(E, msg);
    }

    /**
     * 构建日志消息
     *
     * @param msg 消息
     * @return
     */
    private static void buildMessage(int type, String msg) {
        if (mDebuggable) {
            StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
            String tag = "";
            String methodName = "";
            int lineNumber = 0;
            for (int i = 2; i < trace.length; i++) {
                Class<?> clazz = trace[i].getClass();
                if (!clazz.equals(LogUtils.class)) {
                    tag = trace[i].getClassName();
                    methodName = trace[i].getMethodName();
                    lineNumber = trace[i].getLineNumber();
                    break;
                }
            }
            String formatMsg = new Formatter()
                    .format("[%d/%s/%s]: " + msg,
                            lineNumber,
                            Thread.currentThread().getName(),
                            methodName
                    ).toString();
            String formatTag = tag.substring(tag.lastIndexOf('.') + 1);
            printLog(type, formatTag, formatMsg);
        }
    }

    private static void printLog(int type, String tag, String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            int index = 0;
            String sub;
            for (int i = 0; i < countOfSub; i++) {
                sub = msg.substring(index, index + MAX_LEN);
                printSubLog(type, tag, sub);
                index += MAX_LEN;
            }
            printSubLog(type, tag, msg.substring(index, len));
        } else {
            printSubLog(type, tag, msg);
        }
    }

    private static void printSubLog(final int type, final String tag, String msg) {
        switch (type) {
            case V:
                Log.v(tag, msg);
                break;
            default:
            case D:
                Log.d(tag, msg);
                break;
            case I:
                Log.i(tag, msg);
                break;
            case W:
                Log.w(tag, msg);
                break;
            case E:
                Log.e(tag, msg);
                break;
        }
    }
}
