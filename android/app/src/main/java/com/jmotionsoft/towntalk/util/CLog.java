package com.jmotionsoft.towntalk.util;

import android.util.Log;

public class CLog {
    private static final String TAG = "WEPLACE";

    public static void i(String tag, String message){
        Log.i(TAG+": "+tag, message);
    }

    public static void iformat(String tag, String format, Object... args){
        Log.i(TAG+": "+tag, String.format(format, args));
    }

    public static void d(String tag, String message){
        Log.d(TAG+": "+tag, message);
    }

    public static void d(String message){
        Log.d(TAG, message);
    }

    public static void df(String tag, String format, Object... args){
        Log.d(TAG+": "+tag, String.format(format, args));
    }

    public static void df(String format, Object... args){
        Log.d(TAG, String.format(format, args));
    }

    public static void e(String tag, String message, Exception e){
        Log.e(TAG + ": " + tag, message, e);
    }

    public static void e(String tag, String message, Throwable t){
        Log.e(TAG + ": " + tag, message, t);
    }

    public static void e(String tag, String message){
        Log.e(TAG+": "+tag, message);
    }

    public static void e(String tag, Exception e){
        Log.e(TAG+": "+tag, e.getMessage(), e);
    }

    public static void e(Exception e){
        Log.e(TAG, e.getMessage(), e);
    }
}
