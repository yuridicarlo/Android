package it.bancomat.pay.consumer.network.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.response.DtoRefreshTokenResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public abstract class RefreshTokenTask<E> extends ExtendedTask<E> {

    public RefreshTokenTask(OnCompleteResultListener<E> mListener) {
        super(mListener);
    }

    public boolean managedErrorRefresh(Error error) {
        if (error instanceof HttpError) {
            HttpError httpError = (HttpError) error;
            if (httpError.getCode() == 401) {

                Single.fromCallable(new AppHandleRequestInteractor<Void, DtoRefreshTokenResponse>(DtoRefreshTokenResponse.class, null, AppCmd.REFRESH_TOKEN, getJsessionClient()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ObserverSingleCustom<>(listener));
                return true;
            }
        }
        return false;
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoRefreshTokenResponse>> listener = new NetworkListener<DtoRefreshTokenResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoRefreshTokenResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                AppBancomatDataManager.getInstance().putTokens(response.getRes().getTokens().getAuthorizationToken().getToken(), response.getRes().getTokens().getRefreshToken().getToken());
                start();
            } else {
                sendError(new HttpError(401));
            }
        }

        @Override
        public void onCompleteWithError(Error e) {
            if (e instanceof HttpError) {
                HttpError error = (HttpError) e;
                if (error.getCode() == 401) {
                    task.sendError(new ExtendedError(null, StatusCode.Server.EXIT_APP));
                } else {
                    sendError(error);
                }
            } else {
                CustomLogger.d(task.getClass().getSimpleName(), e.toString());
                task.sendError(e);
            }
        }
    };

}
