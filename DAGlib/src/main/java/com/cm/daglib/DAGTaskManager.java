package com.cm.daglib;


import android.content.Context;
import android.os.SystemClock;


import com.cm.daglib.base.DAGRunListener;
import com.cm.daglib.utils.LogUtils;
import com.cm.daglib.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/19 4:35 PM
 * Desc:
 * *****************************************************************
 */
public class DAGTaskManager {
    private Context mContext;
    private CountDownLatch mCountDownLatch;
    private List<DAGTask> mainList;
    private List<DAGTask> threadList;
    private Map<String, List<DAGTask>> dependTaskMap;
    private AtomicInteger finishTaskCount = new AtomicInteger(0);
    private int totalTaskNum;
    private ThreadPoolExecutor threadPoolExecutor;

    private DAGRunListener mDAGRunListener;
    private long startTime;


    public DAGTaskManager setDAGRunListener(DAGRunListener mDAGRunListener) {
        this.mDAGRunListener = mDAGRunListener;
        return this;
    }

    private DAGTaskManager(Builder builder) {
        init(builder);
    }

    private void init(Builder builder) {
        mContext = builder.mContext;
        mCountDownLatch = builder.countDownLatch;
        mainList = builder.mainList;
        threadList = builder.threadList;
        dependTaskMap = builder.dependTaskMap;
        totalTaskNum = builder.allTaskMap.size();
        threadPoolExecutor = builder.threadPoolExecutor == null ? TreadPoolManager.getInstance().cpuThreadPoolExecutor : builder.threadPoolExecutor;
    }

    public void run(int timeOut) {
        startTime = SystemClock.elapsedRealtime();
        for (DAGTask dagTask : threadList) {
            threadPoolExecutor.execute(new DAGRunnable(this, dagTask));
        }
        for (DAGTask dagTask : mainList) {
            new DAGRunnable(this, dagTask).run();
        }
        await(timeOut);
        if (mDAGRunListener != null)
            mDAGRunListener.finishMainThread(SystemClock.elapsedRealtime() - startTime);
    }

    public void run() {
        run(-1);
    }

    private void await(int timeOut) {
        try {
            if (timeOut > 0) {
                mCountDownLatch.await(timeOut, TimeUnit.MILLISECONDS);
            } else {
                mCountDownLatch.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    void taskFinishUpdate(DAGTask dagTask, long executeTime) {
        if (mDAGRunListener != null) mDAGRunListener.finishTask(dagTask, executeTime);
        List<DAGTask> dagTasks = dependTaskMap.get(dagTask.getClass().getName());
        if (dagTasks != null) {
            //通知关联任务 countDown
            for (DAGTask task : dagTasks) {
                task.countDown();
            }
        }
        if (dagTask.needWait()) {
            mCountDownLatch.countDown();
        }
        finishTaskCount.incrementAndGet();
        if (finishTaskCount.get() == totalTaskNum) {
            if (mDAGRunListener != null)
                mDAGRunListener.finishAllTask(SystemClock.elapsedRealtime() - startTime);
        }
    }

    Context getContext() {
        return mContext;
    }


    public static class Builder {
        protected Context mContext;
        private DAGTask cacheTask;
        //所有需执行的Task
        protected Map<String, DAGTask> allTaskMap = new HashMap<>();
        //与Task相关联的Task列表
        protected Map<String, List<DAGTask>> dependTaskMap = new HashMap<>();
        //需要在主线程执行的Task
        protected List<DAGTask> mainList = new ArrayList<>();
        //需要在子线程执行的Task
        protected List<DAGTask> threadList = new ArrayList<>();

        protected ThreadPoolExecutor threadPoolExecutor;

        private CountDownLatch countDownLatch;
        //需要等待的任务总数，用于CountDownLatch
        private final AtomicInteger needWaitCount = new AtomicInteger();

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder debug(boolean debug) {
            LogUtils.DEBUG = debug;
            return this;
        }

        private Builder add(DAGTask dagTask) {
            if (dagTask != null) {
                cacheTask = dagTask;
                allTaskMap.put(dagTask.getClass().getName(), dagTask);
            }
            return this;
        }

        public Builder add(Class<? extends DAGTask> dagTaskClass) {
            DAGTask dagTask = createTask(dagTaskClass);
            return add(dagTask);
        }

        public Builder after(Class<? extends DAGTask> dagTaskClass) {
            DAGTask dagTask = createTask(dagTaskClass);
            allTaskMap.put(dagTask.getClass().getName(), dagTask);
            if (cacheTask != null) {
                cacheTask.afterTask(dagTaskClass.getName());
            }
            return this;
        }

        public Builder ThreadPool(ThreadPoolExecutor threadPoolExecutor) {
            this.threadPoolExecutor = threadPoolExecutor;
            return this;
        }

        private DAGTask createTask(Class<? extends DAGTask> dagTaskClass) {
            DAGTask dagTask = allTaskMap.get(dagTaskClass.getName());
            if (dagTask == null) {
                dagTask = Utils.createDAGTask(dagTaskClass);
                if (dagTask == null) {
                    throw new DAGException("could not create DAGTask:" + dagTaskClass.getSimpleName());
                }
                if (dagTask.needWait()) {
                    needWaitCount.incrementAndGet();
                }
            }
            return dagTask;
        }

        public DAGTaskManager build() {
            List<DAGTask> dagTasks = Utils.DAGSort(allTaskMap, dependTaskMap);
            cacheTask = null;
            for (DAGTask dagTask : dagTasks) {
                if (dagTask.runMainThread()) {
                    mainList.add(dagTask);
                } else {
                    threadList.add(dagTask);
                }
            }
            countDownLatch = new CountDownLatch(needWaitCount.get());
            return new DAGTaskManager(this);
        }
    }
}
