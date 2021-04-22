package com.example.dagtask;

import android.app.Application;
import android.util.Log;

import com.example.dagtask.task.FiveTask;
import com.example.dagtask.task.FourTask;
import com.example.dagtask.task.OneTask;
import com.example.dagtask.task.ThreeTask;
import com.example.dagtask.task.TwoTask;
import com.cm.daglib.DAGTaskManager;
import com.cm.daglib.DAGTask;
import com.cm.daglib.base.DAGRunListener;

/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/19 5:00 PM
 * Desc:
 * *****************************************************************
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        test();
    }

    private void test() {
        new DAGTaskManager.Builder(this)
                .add(OneTask.class)
                .add(TwoTask.class).after(OneTask.class)
                .add(FourTask.class).after(OneTask.class)
                .add(ThreeTask.class).after(FourTask.class).after(TwoTask.class)
                .add(FiveTask.class).after(FourTask.class).after(ThreeTask.class)
                .build()
                .setDAGRunListener(new DAGRunListener() {
                    @Override
                    public void finishTask(DAGTask dagTask, long executeTime) {
                        Log.e("Chaman",dagTask.toString()+"===>"+executeTime);
                    }

                    @Override
                    public void finishMainThread(long elapsedTime) {
                        Log.e("Chaman","MainThread:"+elapsedTime);
                    }

                    @Override
                    public void finishAllTask(long elapsedTime) {
                        Log.e("Chaman","AllTask:"+elapsedTime);
                    }
                })
                .run();
    }
}
