package com.cm.daglib;

import com.cm.daglib.utils.LogUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/21 2:07 PM
 * Desc:
 * *****************************************************************
 */
class TreadPoolManager {
    private static volatile TreadPoolManager instance;

    public ThreadPoolExecutor cpuThreadPoolExecutor;

    private TreadPoolManager() {
        int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        int MAX_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 5));
        int CORE_POOL_SIZE = Math.max(2, (int) Math.ceil(MAX_POOL_SIZE * 1.0 / 2));
        long KEEP_ALIVE_SECONDS = 5L;
        BlockingQueue<Runnable> mPoolWorkQueue = new LinkedBlockingQueue<>();

        LogUtils.D("CPU_COUNT:" + CPU_COUNT + "  MAX_POOL_SIZE:" + MAX_POOL_SIZE + "  CORE_POOL_SIZE:" + CORE_POOL_SIZE);
        cpuThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, mPoolWorkQueue);
        cpuThreadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    public static TreadPoolManager getInstance() {
        if (instance == null) {
            synchronized (TreadPoolManager.class) {
                if (instance == null) {
                    instance = new TreadPoolManager();
                }
            }
        }
        return instance;
    }


}
