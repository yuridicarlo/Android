package it.bancomatpay.sdk;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class RetrySessionTaskManager {

    private static final String TAG = RetrySessionTaskManager.class.getSimpleName();

    private static RetrySessionTaskManager instance;

    private List<Task<?>> taskList;

    public static RetrySessionTaskManager getInstance() {
        if (instance == null) {
            instance = new RetrySessionTaskManager();
        }
        return instance;
    }

    public void addRetrySessionTask(Task<?> task) {
        CustomLogger.d(TAG, "Task " + task.getClass().getName() + " added = " + task);
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        taskList.add(task);
    }

    public List<Task<?>> getRetrySessionTaskList() {
        return taskList;
    }

    public void removeTaskFromList(Task<?> task) {
        boolean removed = taskList.remove(task);
        CustomLogger.d(TAG, "Task " + task.getClass().getName() + " removed = " + removed);
    }

    public boolean contains(Task<?> task) {
        if(taskList!=null) {
            for (Task<?> t : taskList) {
                if (t.equals(task)) {
                    CustomLogger.d("ABCDX: "+TAG, "Task already present 1!");
                    return true;
                }
            }
        }
        return false;
    }

}
