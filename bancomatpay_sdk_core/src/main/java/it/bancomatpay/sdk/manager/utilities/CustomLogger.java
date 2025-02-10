package it.bancomatpay.sdk.manager.utilities;

import android.util.Log;

import it.bancomatpay.sdk.BuildConfig;

public class CustomLogger {

    public static void v(String tag, String msg, Throwable e) {
        log(Log.VERBOSE, tag, msg, e);
    }

    public static void v(String tag, String msg) {
        log(Log.VERBOSE, tag, msg, null);
    }

    public static void v(String tag, Throwable e) {
        log(Log.VERBOSE, tag, null, e);
    }

    public static void d(String tag, String msg, Throwable e) {
        log(Log.DEBUG, tag, msg, e);
    }

    public static void d(String tag, String msg) {
        log(Log.DEBUG, tag, msg, null);
    }

    public static void d(String tag, Throwable e) {
        log(Log.DEBUG, tag, null, e);
    }

    public static void i(String tag, String msg, Throwable e) {
        log(Log.INFO, tag, msg, e);
    }

    public static void i(String tag, String msg) {
        log(Log.INFO, tag, msg, null);
    }

    public static void i(String tag, Throwable e) {
        log(Log.INFO, tag, null, e);
    }

    public static void w(String tag, String msg, Throwable e) {
        log(Log.WARN, tag, msg, e);
    }

    public static void w(String tag, String msg) {
        log(Log.WARN, tag, msg, null);
    }

    public static void w(String tag, Throwable e) {
        log(Log.WARN, tag, null, e);
    }

    public static void e(String tag, String msg, Throwable e) {
        log(Log.ERROR, tag, msg, e);
    }

    public static void e(String tag, String msg) {
        log(Log.ERROR, tag, msg, null);
    }

    public static void e(String tag, Throwable e) {
        log(Log.ERROR, tag, null, e);
    }

    public static void a(String tag, String msg, Throwable e) {
        log(Log.ASSERT, tag, msg, e);
    }

    public static void a(String tag, String msg) {
        log(Log.ASSERT, tag, msg, null);
    }

    public static void a(String tag, Throwable e) {
        log(Log.ASSERT, tag, null, e);
    }

    private static final int MAX_LOG_LENGTH = 4000;

    private static void log(int level, String tag, String message, Throwable t) {
        if (BuildConfig.DEBUG) {
            if (tag == null) {
                tag = "PayLog";
            }
            if (message == null) {
                message = "";
            }
            if (t != null) message = message + '\n' + Log.getStackTraceString(t);
            int counter = 0;
            // Split by line, then ensure each line can fit into Log's maximum length.
            for (int i = 0, length = message.length(); i < length; i++) {
                int newline = message.indexOf('\n', i);
                newline = newline != -1 ? newline : length;
                do {
                    int end = Math.min(newline, i + MAX_LOG_LENGTH);
                    Log.println(level, tag, message.substring(i, end));
                    i = end;
                    counter++;
                } while (i < newline);
            }
            if (counter > 1) {
                Log.println(level, tag, "<-- END LOG (" + counter + " lines)");
            }
        }
    }

}

