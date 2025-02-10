package it.bancomat.pay.consumer.network.task;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.network.dto.PinOperationType;
import it.bancomat.pay.consumer.network.dto.VerifyPinData;
import it.bancomat.pay.consumer.network.dto.request.DtoLoginRequest;
import it.bancomat.pay.consumer.network.dto.request.DtoLoginRequestUnencrypted;
import it.bancomat.pay.consumer.network.dto.request.DtoPinCrypted;
import it.bancomat.pay.consumer.network.dto.response.DtoLoginResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.network.interactor.DtoPinCryptedInteractor;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class LoginTask extends PinCryptedTask<VerifyPinData> {

    public LoginTask(OnCompleteResultListener<VerifyPinData> mListener, AppAuthenticationInterface authenticationInterface) {
        super(mListener);
        this.authenticationInterface = authenticationInterface;
    }

    @Override
    protected void start() {
        DtoLoginRequestUnencrypted dtoLoginRequestUnencrypted = new DtoLoginRequestUnencrypted();
        dtoLoginRequestUnencrypted.setOperation(PinOperationType.LOGIN_WITH_PIN); //forse verra runissi
        dtoLoginRequestUnencrypted.setTimestamp(Long.toString(System.currentTimeMillis()));


        Single.fromCallable(new DtoPinCryptedInteractor<>(authenticationInterface, dtoLoginRequestUnencrypted))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<DtoPinCrypted>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull DtoPinCrypted dtoPinCrypted) {
                        DtoLoginRequest req = new DtoLoginRequest();
                        req.setDtoPinCrypted(dtoPinCrypted);

                        Single.fromCallable(new AppHandleRequestInteractor<>(DtoLoginResponse.class, req, AppCmd.LOGIN, getJsessionClient()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new ObserverSingleCustom<>(l));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<Void> resultError = new Result<>();
                        resultError.setStatusCodeMessage(e.getMessage());
                        sendCompletition(resultError);
                        CustomLogger.e(TAG, "Error in complete DtoPinCryptedInteractor: " + e.getMessage());
                    }
                });
    }

    OnNetworkCompleteListener<DtoAppResponse<DtoLoginResponse>> l = new AppNetworkListener<DtoLoginResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoLoginResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<VerifyPinData> r = new Result<>();
            prepareResult(r, response);
            VerifyPinData verifyPinData = new VerifyPinData();
            r.setResult(verifyPinData);
            if (r.isSuccess()) {
                AppBancomatDataManager.getInstance().putTokens(response.getRes().getTokens().getAuthorizationToken().getToken(), response.getRes().getTokens().getRefreshToken().getToken());
                BancomatFullStackSdkInterfaceExtended.Factory.getInstance().setLoyaltyToken(response.getRes().getLoyaltyToken());
            } else {
                try {
                    verifyPinData.setLastAttempts(Integer.parseInt(response.getRes().getLastAttempts()));
                } catch (Exception e) {
                    verifyPinData.setLastAttempts(-1);
                }
            }
            sendCompletition(r);
        }

    };

}
