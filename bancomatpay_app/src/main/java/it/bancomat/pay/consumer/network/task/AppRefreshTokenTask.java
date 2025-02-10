package it.bancomat.pay.consumer.network.task;

import androidx.annotation.NonNull;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.response.DtoRefreshTokenResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class AppRefreshTokenTask extends ExtendedTask<Void> {

    public AppRefreshTokenTask(OnCompleteResultListener<Void> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new AppHandleRequestInteractor<Void, DtoRefreshTokenResponse>( DtoRefreshTokenResponse.class, null, AppCmd.REFRESH_TOKEN, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));

    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoRefreshTokenResponse>> listener = new AppNetworkListener<DtoRefreshTokenResponse>(this) {

        @Override
        protected void manageComplete(@NonNull DtoAppResponse<DtoRefreshTokenResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                AppBancomatDataManager.getInstance().putTokens(response.getRes().getTokens().getAuthorizationToken().getToken(), response.getRes().getTokens().getRefreshToken().getToken());
                SessionManager.getInstance().setSessionToken(response.getRes().getTokens().getAuthorizationToken().getToken());
                //start();
            } else {
                if (r.getStatusCode() instanceof StatusCode.Server) {
                    sendError(new ExtendedError(null, r.getStatusCode()));
                } else {
                    sendError(new HttpError(401));
                }
            }
            sendCompletition(r);
        }

        @Override
        public void onCompleteWithError(@NonNull Error e) {
            if (e instanceof HttpError) {
                HttpError error = (HttpError) e;
                if (error.getCode() == 401) {
                    task.sendError(new ExtendedError(null, StatusCode.Server.EXIT_APP));
                } else {
                    sendError(error);
                }
            } else {
                CustomLogger.e(task.getClass().getSimpleName(), e.toString());
                task.sendError(e);
            }
        }
    };

}
