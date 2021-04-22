package com.cm.daglib.base;

import com.cm.daglib.DAGTask;

/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/21 3:19 PM
 * Desc:
 * *****************************************************************
 */
public interface DAGRunListener {

    //单个任务执行完成 耗时
    void finishTask(DAGTask dagTask, long executeTime);

    //主线程任务执行完成耗时
    void finishMainThread(long elapsedTime);

    //所有任务执行完成耗时
    void finishAllTask(long elapsedTime);

}
