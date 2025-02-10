package it.bancomat.pay.consumer.network;

import it.bancomat.pay.consumer.network.task.RefreshTokenTask;
import it.bancomatpay.sdk.RetrySessionTaskManager;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public abstract class NetworkRefreshListener<E> extends NetworkListener<E> {

    private static final String TAG = NetworkRefreshListener.class.getSimpleName();

    protected NetworkRefreshListener(RefreshTokenTask<?> task) {
        super(task);
    }

    @Override
    public void onCompleteWithError(Error e) {
        CustomLogger.d(TAG, task.getClass().getSimpleName() + " on error: " + e.toString());
        boolean managedError = ((RefreshTokenTask<?>) task).managedErrorRefresh(e);
        if (!managedError) {
            CustomLogger.d(task.getClass().getSimpleName(), e.toString());
            task.sendError(e);
        } else {
            if(RetrySessionTaskManager.getInstance().contains(task)) {
                task.sendError(new Error());
            } else {
                RetrySessionTaskManager.getInstance().addRetrySessionTask(task);
                CustomLogger.d(TAG,
                        "Adding task " + task.getClass().getSimpleName() + " to RetrySessionTaskManager list");
                task.sendErrorSessionExpired();
            }

        }
        CustomLogger.d(TAG, "Task managed error = " + managedError);
    }

}
