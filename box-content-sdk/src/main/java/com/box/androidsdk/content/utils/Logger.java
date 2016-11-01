package com.box.androidsdk.content.utils;

import java.util.Map;

/**
 * Interface supporting logging for box sdks
 */

public interface Logger {

    /**
     *  @return true if logging is currently enabled.
     */
    public boolean getIsLoggingEnabled();

    /**
     * Info logs.
     * @param tag tag to provide category information
     * @param msg detailed message for debugging
     */
    public void i(String tag, String msg);

    /**
     * Info logs with key-value pairs
     * @param tag tag to provide category information
     * @param msg detailed message for debugging
     * @param map map of keys and values to log.
     */
    public void i(String tag, String msg, Map<String, String> map);

    /**
     * Debug logs
     * @param tag tag to provide category information
     * @param msg detailed message for debugging
     */
    public void d(String tag, String msg);

    /**
     * Error logs
     * @param tag tag to provide category information
     * @param msg detailed message for debugging
     */
    public void e(String tag, String msg);

    /**
     * Error log with throwable
     * @param tag tag to provide category information
     * @param t the exception to log
     */
    public void e(String tag, Throwable t);

    /**
     * Error log with throwable and message.
     * @param tag tag to provide category information
     * @param msg detailed message for debugging
     * @param t the exception to log
     */
    public void e(String tag, String msg, Throwable t);

    /**
     * Use for logging non fatal errors
     * May be used for debugging with stack traces.
     * @param tag tag to provide category information
     * @param msg detailed message for debugging
     * @param t the exception if applicable
     */
    public void nonFatalE(String tag, String msg, Throwable t);
}
