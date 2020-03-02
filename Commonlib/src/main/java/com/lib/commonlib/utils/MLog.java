package com.lib.commonlib.utils;

import android.util.Log;


public final class MLog {
    /* 留log保存文档后门，默认状态不保存到文件*/
    public static boolean logSaveToFileFlag = false;

    public static boolean enable = true;
    public static int sLevel = 5;
    public static String logTag = "RealTimeVoice";

    public static String getLogTag() {
        return logTag;
    }

    public static void setLogTag(String logTag) {
        MLog.logTag = logTag;
    }

    public static void setLogEnable(boolean isEnable) {
        enable = isEnable;
    }

    public static void setLogLevel(int level) {
        switch (level) {
            case 0:
                setLogEnable(false);
                sLevel = level;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                setLogEnable(true);
                sLevel = level;
                break;
            default:
                setLogEnable(true);
                sLevel = 5;
                break;
        }
    }

    public static void v(String tag, String msg) {
        if (enable) {
            if (sLevel > 4)
                Log.v(tag, "" + msg);
        }
    }

    public static void v(String msg){
        v(logTag,msg);
    }

    public static void d(String tag, String msg) {
        if (enable) {
            if (sLevel > 3)
                Log.d(tag, "" + msg);
        }
    }

    public static void d(String msg){
        i(logTag,msg);
    }

    public static void i(String tag, String msg) {
        if (enable) {
            if (sLevel > 2)
                Log.i(tag, "" + msg);
        }
    }

    public static void i(String msg){
        i(logTag,msg);
    }

    public static void w(String tag, String msg) {
        if (enable) {
            if (sLevel > 1)
                Log.w(tag, "" + msg);
        }
    }

    public static void w(String msg){
        w(logTag,msg);
    }

    public static void e(String tag, String msg) {
        if (enable) {
            if (sLevel > 0)
                Log.e(tag, "" + msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (enable) {
            if (sLevel > 0)
                Log.e(tag, "" + msg,e);
        }
    }

    public static void e(String msg){
        e(logTag,msg);
    }

    public static void e(String msg, Throwable e){
        e(logTag,msg,e);
    }
}
