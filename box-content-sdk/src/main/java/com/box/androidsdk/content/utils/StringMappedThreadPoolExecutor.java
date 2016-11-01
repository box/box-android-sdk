package com.box.androidsdk.content.utils;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * A thread pool executor that lets callers access a runnable by its toString method.
 */
public final class StringMappedThreadPoolExecutor extends ThreadPoolExecutor {

    private final ConcurrentHashMap<String, WeakReference<Runnable>> mRunningTasks = new ConcurrentHashMap<String, WeakReference<Runnable>>();

    public StringMappedThreadPoolExecutor(int corePoolSize,
                                          int maximumPoolSize,
                                          long keepAliveTime,
                                          TimeUnit unit,
                                          BlockingQueue<Runnable> workQueue,
                                          ThreadFactory threadFactory){
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        mRunningTasks.put(r.toString(), new WeakReference(r));
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        mRunningTasks.remove(r.toString());
    }

    public Runnable getTaskFor(String runnableString){
        WeakReference<Runnable> ref = mRunningTasks.get(runnableString);
        if (ref == null){
            return null;
        }
        return ref.get();
    }

}
