package com.jerry_mar.httputils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskUtils {
    private static TaskUtils instance;

    public static TaskUtils getInstance() {
        if (instance == null) {
            instance = new TaskUtils();
        }
        return instance;
    }

    static void addTask(String key, RequestTask task) {
        TaskUtils instance = getInstance();
        List<RequestTask> list = instance.stack.get(key);
        if (list == null) {
            list = new LinkedList<>();
            instance.stack.put(key, list);
        }
        list.add(task);
    }

    public static boolean contains(String key, RequestTask task) {
        TaskUtils instance = getInstance();
        List<RequestTask> list = instance.stack.get(key);
        if (list != null) {
            return list.contains(task);
        }
        return false;
    }

    public static void removeTask(String key) {
        TaskUtils instance = getInstance();
        List<RequestTask> task = instance.stack.get(key);
        if (task != null) {
            instance.stack.remove(key);
            for (int i = 0; i < task.size(); i++) {
                task.get(i).destroy();
            }
        }
    }

    public static boolean removeTask(String key, RequestTask task) {
        TaskUtils instance = getInstance();
        List<RequestTask> list = instance.stack.get(key);
        if (list != null) {
            return list.remove(task);
        }
        return false;
    }

    private Map<String, List<RequestTask>> stack;

    private TaskUtils() {
        stack = new ConcurrentHashMap<>();
    }
}
