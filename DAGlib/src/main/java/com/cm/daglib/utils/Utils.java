package com.cm.daglib.utils;

import com.cm.daglib.DAGException;
import com.cm.daglib.DAGTask;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static DAGTask createDAGTask(Class<? extends DAGTask> dagTaskClass) {
        try {
            return dagTaskClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<DAGTask> DAGSort(Map<String, DAGTask> allTaskMap, Map<String, List<DAGTask>> dependTaskMap) {
        List<DAGTask> result = new ArrayList<>();
        ArrayDeque<DAGTask> independentQueue = new ArrayDeque<>();
        Map<String, Integer> taskDependSizeMap = new HashMap<>(); //每个Task的入度表

        //获取每个task的入度
        for (Map.Entry<String, DAGTask> taskEntry : allTaskMap.entrySet()) {
            DAGTask dagTask = taskEntry.getValue();
            int size = dagTask.getDependsList().size();
            taskDependSizeMap.put(taskEntry.getKey(), size);
            if (size <= 0) {
                independentQueue.add(dagTask);
            }
        }

        //获取Task与依赖其完成的 其他Task数组
        for (Map.Entry<String, DAGTask> taskEntry : allTaskMap.entrySet()) {
            DAGTask dagTask = taskEntry.getValue();
            for (String dependTaskName : dagTask.getDependsList()) {
                List<DAGTask> list = dependTaskMap.get(dependTaskName);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(dagTask);
                dependTaskMap.put(dependTaskName, list);
            }
        }

        logDependMap(dependTaskMap);

        //使用 BFS 方法获得有向无环图的拓扑排序
        while (!independentQueue.isEmpty()) {
            DAGTask dagTask = independentQueue.pop();
            result.add(dagTask);
            List<DAGTask> list = dependTaskMap.get(dagTask.getClass().getName());
            if (list != null) {
                for (DAGTask task : list) {
                    String name = task.getClass().getName();
                    Integer size = taskDependSizeMap.get(name);
                    size--;
                    if (size <= 0) {
                        independentQueue.offer(task);
                    }
                    taskDependSizeMap.put(name, size);
                }
            }
        }

        //如果结果size不相等，说明是有环的
        if (result.size() != allTaskMap.size()) {
            throw new DAGException("Ring appeared！");
        }

        logResult(result);

        return result;
    }

    private static void logDependMap(Map<String, List<DAGTask>> dependTaskMap) {
        if (!LogUtils.DEBUG || dependTaskMap == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("DependMap:【\n");
        for (Map.Entry<String, List<DAGTask>> entry : dependTaskMap.entrySet()) {
            sb.append("\t").append(entry.getKey()).append(" => ");
            List<DAGTask> value = entry.getValue();
            if (value != null) {
                sb.append("[").append("\n");
                for (DAGTask dagTask : value) {
                    sb.append("\t\t").append(dagTask.toString()).append(",\n");
                }
                sb.append("\t").append("]\n");
            } else {
                sb.append("[]\n");
            }
        }
        sb.append("】");
        LogUtils.D(sb.toString());
    }

    private static void logResult(List<DAGTask> result) {
        if (!LogUtils.DEBUG || result == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("SortResult【\n");
        for (DAGTask dagTask : result) {
            sb.append("\t").append(dagTask.toString()).append(" ==> \n");
        }
        sb.append("】");
        LogUtils.D(sb.toString());
    }
}
