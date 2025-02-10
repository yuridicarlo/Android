package it.bancomat.pay.consumer.network.task;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.network.dto.request.DtoGetAuthorizationTokenRequest;
import it.bancomat.pay.consumer.network.dto.request.DtoGetAuthorizationTokenUnencrypted;
import it.bancomat.pay.consumer.network.dto.request.DtoPinCrypted;
import it.bancomat.pay.consumer.network.dto.response.DtoGetAuthorizationTokenResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.network.interactor.DtoPinCryptedInteractor;
import it.bancomatpay.sdk.LoyaltyTokenManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class GetAuthorizationTokenTask extends PinCryptedTask<DtoGetAuthorizationTokenResponse> {

    private final AppAuthenticationInterface authenticationInterface;
    private final String pinOperationType;
    private final String amount;
    private final String msisdnSender;
    private final String receiver;
    private final String operationId;

    public GetAuthorizationTokenTask(OnCompleteResultListener<DtoGetAuthorizationTokenResponse> mListener, AppAuthenticationInterface authenticationInterface, AuthenticationOperationType pinOperationType, String operationId, String amount, String msisdnSender, String receiver) {
        super(mListener);
        this.authenticationInterface = authenticationInterface;
        this.pinOperationType = pinOperationType.toString();
        this.operationId = operationId;
        this.amount = amount;
        this.msisdnSender = msisdnSender;
        this.receiver = receiver;
    }

    @Override
    protected void start() {
        DtoGetAuthorizationTokenUnencrypted dtoGetAuthorizationTokenUnencrypted = new DtoGetAuthorizationTokenUnencrypted();
        dtoGetAuthorizationTokenUnencrypted.setOperation(pinOperationType);
        dtoGetAuthorizationTokenUnencrypted.setOperationId(operationId);
        dtoGetAuthorizationTokenUnencrypted.setAmount(amount);
        dtoGetAuthorizationTokenUnencrypted.setSender(msisdnSender);
        dtoGetAuthorizationTokenUnencrypted.setReceiver(receiver);

        String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss").format(new Date());
        dtoGetAuthorizationTokenUnencrypted.setTimestamp(currentTimestamp);

        Single.fromCallable(new DtoPinCryptedInteractor<>(authenticationInterface, dtoGetAuthorizationTokenUnencrypted))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<DtoPinCrypted>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull DtoPinCrypted dtoPinCrypted) {
                        DtoGetAuthorizationTokenRequest req = new DtoGetAuthorizationTokenRequest();
                        req.setDtoPinCrypted(dtoPinCrypted);

                        Single.fromCallable(new AppHandleRequestInteractor<>(DtoGetAuthorizationTokenResponse.class, req, AppCmd.GET_AUTHORIZATION_TOKEN, getJsessionClient()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new ObserverSingleCustom<>(listener2));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<DtoGetAuthorizationTokenResponse> r = new Result<>();
                        r.setStatusCode(StatusCode.Mobile.GENERIC_ERROR);
                        sendCompletition(r);
                        CustomLogger.e(TAG, "Error on DtoPinCryptedInteractor: " + e.getMessage());
                    }
                });


    }

    private final OnNetworkCompleteListener<DtoAppResponse<DtoGetAuthorizationTokenResponse>> listener2 = new AppNetworkListener<DtoGetAuthorizationTokenResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetAuthorizationTokenResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<DtoGetAuthorizationTokenResponse> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess() && !TextUtils.isEmpty(response.getRes().getLoyaltyToken())) {
                LoyaltyTokenManager.getInstance().setLoyaltyToken(response.getRes().getLoyaltyToken());
            }
            r.setResult(response.getRes());
            sendCompletition(r);
        }

    };

}
