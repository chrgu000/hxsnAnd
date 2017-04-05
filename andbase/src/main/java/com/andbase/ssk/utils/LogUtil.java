package com.andbase.ssk.utils;

import android.util.Log;

/**
 *
 * Created by jiely on 2017/3/24.
 */
public class LogUtil {

    /** info开关. */
    public static boolean MY_LOG_INFO = true;

    /**
     * info日志
     * @param tag
     * @param message
     */
    public static void i(String tag,String message) {
        if(MY_LOG_INFO)
            Log.i(tag, message);
    }

    /**
     * info日志
     * @param clazz
     * @param message
     */
    public static void i(Class<?> clazz,String message) {
        String tag = clazz.getSimpleName();
        if(MY_LOG_INFO)
            i(tag, message);
    }


    /**
     * info日志
     * @param tag
     * @param message
     */
    public static void ii(String tag,String message) {
        Log.i(tag, message);
    }

    /**
     * info日志
     * @param clazz
     * @param message
     */
    public static void ii(Class<?> clazz,String message) {
        String tag = clazz.getSimpleName();
        i(tag, message);
    }

    public static void closeLog(){
        MY_LOG_INFO = false;
    }

    public static void openLog(){
        MY_LOG_INFO = true;
    }
}
