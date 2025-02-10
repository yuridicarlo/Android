package it.bancomat.pay.consumer.utilities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

import it.bancomat.pay.consumer.network.task.SetPinTask;
import it.bancomatpay.sdk.core.OnCompleteListener;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;

public class ActivityTaskManager extends AppCompatActivity implements OnCompleteListener {

    Set<Task<?>> taskSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskSet = new HashSet<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Task<?> task : taskSet) {
            task.removeListener();
        }
        taskSet.clear();
    }

    public synchronized void addTask(Task<?> task) {
        if (!taskSet.contains(task)) {
            task.setMasterListener(this);
            taskSet.add(task);
            //RetrySessionTaskManager.getInstance().addRetrySessionTask(task);
            task.execute();
        }
    }

    @Override
    public void onComplete(Task<?> task) {
        task.removeListener();
        taskSet.remove(task);
        if (!(task instanceof SetPinTask)) {
            LoaderHelper.dismissLoader();
        }
    }

    @Override
    public void onCompleteWithError(Task<?> task, Error e) {
        task.removeListener();
        taskSet.remove(task);
        LoaderHelper.dismissLoader();
    }

}