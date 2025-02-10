package it.bancomatpay.sdk.manager.task;

import androidx.annotation.NonNull;

import java.util.HashSet;

import it.bancomatpay.sdk.RetrySessionTaskManager;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeWrapper;

public abstract class NetworkListener<E> implements OnNetworkCompleteListener<DtoAppResponse<E>> {

    private static final String TAG = NetworkListener.class.getSimpleName();

    protected ExtendedTask<?> task;

    private static HashSet<StatusCodeInterface> centralized = new HashSet<>();

    static {
        centralized.add(StatusCode.Server.EXIT_APP);
        centralized.add(StatusCode.Server.P2P_USER_NOT_ENABLED_ON_BCM_PAY);
        centralized.add(StatusCode.Server.WRONG_APP_VERSION);
    }

    public NetworkListener(ExtendedTask<?> task) {
        this.task = task;
    }

    @Override
    public void onCompleteWithError(Error e) {
        CustomLogger.d(TAG, task.getClass().getSimpleName() + " on error: " + e.toString());
        boolean managedError = (task).managedError(e);
        if (!managedError) {
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

    @Override
    public void onComplete(@NonNull DtoAppResponse<E> response) {
        StatusCodeWrapper server = Mapper.getStatusCodeWrapper(response.getDtoStatus());
        if (centralized.contains(server.getStatusCodeWrapped())) {
            task.sendError(new ExtendedError(null, server.getStatusCodeWrapped()));
        } else {
            manageComplete(response);
        }
    }

    protected abstract void manageComplete(DtoAppResponse<E> response);

}
