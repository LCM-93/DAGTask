package com.example.dagtask.task;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.cm.daglib.DAGTask;

/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/19 5:14 PM
 * Desc:
 * *****************************************************************
 */
public class FourTask extends DAGTask {
    @Override
    public void run(Context context) {
        SystemClock.sleep(300);
        Log.e("TASK","FourTask run finish");
    }

}
