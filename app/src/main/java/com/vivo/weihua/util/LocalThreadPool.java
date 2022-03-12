package com.vivo.weihua.util;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalThreadPool {
    private static LocalThreadPool mInstance;
    private ThreadPoolExecutor threadPool;
    private static int MAX_CORE_NUMBER = Runtime.getRuntime().availableProcessors();
    private LocalThreadPool() {
        threadPool = new ThreadPoolExecutor(MAX_CORE_NUMBER, 10, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            AtomicInteger auto = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                // TODO Auto-generated method stub
                Thread thread = new Thread(r, "crate-thread-" + auto.incrementAndGet());
                return thread;
            }

        });
        threadPool.allowCoreThreadTimeOut(true);
    }

    public static LocalThreadPool getInstance() {
        if (mInstance == null) {
            synchronized (LocalThreadPool.class) {
                if (mInstance == null) {
                    mInstance = new LocalThreadPool();
                }
            }

        }
        return mInstance;
    }

    public Future<?> submit(Runnable runnable) {
        return threadPool.submit(runnable);
    }

    public int count() {
        return threadPool.getActiveCount();
    }

}
