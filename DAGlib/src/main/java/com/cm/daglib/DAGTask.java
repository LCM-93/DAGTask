package com.cm.daglib;

import com.cm.daglib.base.IDAGTask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import android.os.Process;

/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/19 4:44 PM
 * Desc:
 * *****************************************************************
 */
public abstract class DAGTask implements IDAGTask {

    private final Set<String> dependsList = new HashSet<>();
    private CountDownLatch countDownLatch;

    public Set<String> getDependsList() {
        return dependsList;
    }

    @Override
    public boolean runMainThread() {
        return false;
    }

    @Override
    public boolean needWait() {
        return false;
    }

    @Override
    public int priority() {
        return Process.THREAD_PRIORITY_FOREGROUND;
    }

    protected void afterTask(String name) {
        dependsList.add(name);
    }

    protected void await() {
        tryToInitCountDown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void countDown() {
        tryToInitCountDown();
        countDownLatch.countDown();
    }


    private void tryToInitCountDown() {
        if (countDownLatch == null) {
            countDownLatch = new CountDownLatch(dependsList.size());
        }
    }

}
