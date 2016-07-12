package com.box.androidsdk.content.utils;

import android.util.Log;

import com.box.androidsdk.content.BoxConfig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation for Logger.
 */
public class BoxLogger implements Logger {
    public  boolean getIsLoggingEnabled() {
        return (BoxConfig.IS_LOG_ENABLED && BoxConfig.IS_DEBUG);
    }

    public  void i(String tag, String msg) {
        if (getIsLoggingEnabled()) {
            Log.i(tag, msg);
        }
    }

    public  void i(String tag, String msg, Map<String, String> map) {
        if (getIsLoggingEnabled() && map != null) {
            for (Map.Entry<String,String> e : map.entrySet()) {
                Log.i(tag, String.format(Locale.ENGLISH, "%s:  %s:%s", msg, e.getKey(), e.getValue()));
            }
        }
    }

    public  void d(String tag, String msg) {
        if (getIsLoggingEnabled()) {
            Log.d(tag, msg);
        }
    }

    public  void e(String tag, String msg) {
        if (getIsLoggingEnabled()) {
            Log.e(tag, msg);
        }
    }

    public  void e(String tag, Throwable t) {
        if (getIsLoggingEnabled() && t != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            Log.e(tag, sw.toString());
        }
    }

    public  void e(String tag, String msg, Throwable t) {
        if (getIsLoggingEnabled()) {
            Log.e(tag, msg, t);
        }
    }

    @Override
    public void nonFatalE(String tag, String msg, Throwable t) {
        if (getIsLoggingEnabled()) {
            Log.e("NON_FATAL" + tag, msg, t);
        }
    }
}
