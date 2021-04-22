package com.cm.daglib;


import android.os.SystemClock;

/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/21 1:26 PM
 * Desc:
 * *****************************************************************
 */
class DAGRunnable implements Runnable {

    private DAGTaskManager dagTaskManager;
    private DAGTask dagTask;

    public DAGRunnable(DAGTaskManager dagTaskManager, DAGTask dagTask) {
        this.dagTaskManager = dagTaskManager;
        this.dagTask = dagTask;
    }

    @Override
    public void run() {
        dagTask.await();
        long startTime = SystemClock.elapsedRealtime();
        dagTask.run(dagTaskManager.getContext());
        long executeTime = SystemClock.elapsedRealtime() - startTime;
        dagTaskManager.taskFinishUpdate(dagTask, executeTime);
    }
}
