package com.cm.daglib.base;

import android.content.Context;

/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/19 4:32 PM
 * Desc:
 * *****************************************************************
 */
public interface IDAGTask {

    //是否在主线程执行
    boolean runMainThread();

    //执行任务
    void run(Context context);

    //是否需要主线程等待
    boolean needWait();

    //线程优先级
    int priority();
}
