package it.bancomat.pay.consumer.network.task;

import androidx.annotation.NonNull;

import java.util.HashSet;

import it.bancomat.pay.consumer.utilities.AppMapper;
import it.bancomat.pay.consumer.utilities.statuscode.AppStatusCode;
import it.bancomat.pay.consumer.utilities.statuscode.AppStatusCodeWrapper;
import it.bancomatpay.sdk.RetrySessionTaskManager;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeWrapper;

public abstract class AppNetworkListener<E> implements OnNetworkCompleteListener<DtoAppResponse<E>> {

    private static final String TAG = AppNetworkListener.class.getSimpleName();

    protected ExtendedTask<?> task;

    private static HashSet<StatusCodeInterface> centralized = new HashSet<>();
    private static HashSet<StatusCodeInterface> centralizedExtended = new HashSet<>();

    static {
        centralized.add(StatusCode.Server.EXIT_APP);
        centralized.add(StatusCode.Server.P2P_USER_NOT_ENABLED_ON_BCM_PAY);
        centralized.add(StatusCode.Server.WRONG_APP_VERSION);
        centralizedExtended.add(AppStatusCode.ServerExtended.PIN_LOCKED);
    }

    AppNetworkListener(ExtendedTask<?> task) {
        this.task = task;
    }

    @Override
    public void onCompleteWithError(@NonNull Error e) {
        CustomLogger.d(task.getClass().getSimpleName(), e.toString());
        if (!(task).managedError(e)) {
            task.sendError(e);
        } else {
            RetrySessionTaskManager.getInstance().addRetrySessionTask(task);
            CustomLogger.d(TAG,
                    "Adding task " + task.getClass().getSimpleName() + " to RetrySessionTaskManager list");
            task.sendErrorSessionExpired();
        }
    }

    @Override
    public void onComplete(@NonNull DtoAppResponse<E> response) {
        StatusCodeWrapper server = Mapper.getStatusCodeWrapper(response.getDtoStatus());
        AppStatusCodeWrapper serverExtended = AppMapper.getStatusCodeWrapper(response.getDtoStatus());
        if (centralized.contains(server.getStatusCodeWrapped())) {
            task.sendError(new ExtendedError(null, server.getStatusCodeWrapped()));
        } else if (centralizedExtended.contains(serverExtended.getStatusCodeWrapped())) {
            task.sendError(new ExtendedError(null, serverExtended.getStatusCodeWrapped()));
        } else {
            manageComplete(response);
        }
    }

    protected abstract void manageComplete(DtoAppResponse<E> response);

}
