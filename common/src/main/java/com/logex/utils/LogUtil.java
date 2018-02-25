package com.logex.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * 强大的日志产生工具类
 */
public class LogUtil {
    public static int LOG_LEVEL = 6; //开发时测试设为6，应用发布时改为0
    private final static int ERROR = 1;
    private final static int WARN = 2;
    private final static int INFO = 3;
    private final static int DEBUG = 4;
    private final static int VERBOSE = 5;
    private final static String customTagPrefix = "x_log";

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static void e(String msg) {
        String tag = generateTag();
        if (LOG_LEVEL > ERROR)
            Log.e(tag, msg);
    }

    public static void w(String msg) {
        String tag = generateTag();
        if (LOG_LEVEL > WARN)
            Log.w(tag, msg);
    }

    public static void i(String msg) {
        String tag = generateTag();
        if (LOG_LEVEL > INFO)
            Log.i(tag, msg);
    }

    public static void d(String msg) {
        String tag = generateTag();
        if (LOG_LEVEL > DEBUG)
            Log.d(tag, msg);
    }

    public static void v(String msg) {
        String tag = generateTag();
        if (LOG_LEVEL > VERBOSE)
            Log.v(tag, msg);
    }
}
