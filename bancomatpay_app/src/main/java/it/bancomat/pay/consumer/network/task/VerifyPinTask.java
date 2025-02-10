package it.bancomat.pay.consumer.network.task;

import com.google.gson.Gson;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.network.dto.PinOperationType;
import it.bancomat.pay.consumer.network.dto.VerifyPinData;
import it.bancomat.pay.consumer.network.dto.request.DtoPinCrypted;
import it.bancomat.pay.consumer.network.dto.request.DtoVerifyPinRequest;
import it.bancomat.pay.consumer.network.dto.request.DtoVerifyPinRequestUnencrypted;
import it.bancomat.pay.consumer.network.dto.response.DtoVerifyPinResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.network.interactor.DtoPinCryptedInteractor;
import it.bancomat.pay.consumer.storage.model.FingerprintData;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class VerifyPinTask extends PinCryptedTask<VerifyPinData> {

    public VerifyPinTask(OnCompleteResultListener<VerifyPinData> mListener, AppAuthenticationInterface authenticationInterface) {
        super(mListener);
        this.authenticationInterface = authenticationInterface;
    }

    @Override
    protected void start() {
        DtoVerifyPinRequestUnencrypted dtoVerifyPinRequestUnencrypted = new DtoVerifyPinRequestUnencrypted();
        dtoVerifyPinRequestUnencrypted.setOperation(PinOperationType.VERIFY_PIN);
        dtoVerifyPinRequestUnencrypted.setTimestamp(Long.toString(System.currentTimeMillis()));
        Single.fromCallable(new DtoPinCryptedInteractor<>(authenticationInterface, dtoVerifyPinRequestUnencrypted))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<DtoPinCrypted>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull DtoPinCrypted dtoPinCrypted) {
                        DtoVerifyPinRequest req = new DtoVerifyPinRequest();
                        req.setDtoPinCrypted(dtoPinCrypted);

                        Single.fromCallable(new AppHandleRequestInteractor<>(DtoVerifyPinResponse.class, req, AppCmd.VERIFY_PIN, getJsessionClient()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new ObserverSingleCustom<>(l));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<Void> resultError = new Result<>();
                        resultError.setStatusCodeMessage(e.getMessage());
                        sendCompletition(resultError);
                        CustomLogger.e(TAG, "Error on DtoPinCryptedInteractor: " + e.getMessage());
                    }
                });

    }

    OnNetworkCompleteListener<DtoAppResponse<DtoVerifyPinResponse>> l = new AppNetworkListener<DtoVerifyPinResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoVerifyPinResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<VerifyPinData> r = new Result<>();
            VerifyPinData verifyPinData = new VerifyPinData();
            prepareResult(r, response);
            if (r.isSuccess()) {
                FingerprintData fingerprintData = new FingerprintData();
                fingerprintData.setSeed(authenticationInterface.getSeed());
                fingerprintData.setHmacKey(authenticationInterface.getHmacKey());
                Gson gson = new Gson();
                String jsonFingerprint = gson.toJson(fingerprintData, FingerprintData.class);
                verifyPinData.setLastAttempts(-1);
                verifyPinData.setFingerprintData(jsonFingerprint.getBytes());
            } else {

                try {
                    verifyPinData.setLastAttempts(Integer.parseInt(response.getRes().getLastAttempts()));
                } catch (Exception e) {
                    verifyPinData.setLastAttempts(-1);
                }
            }
            r.setResult(verifyPinData);
            sendCompletition(r);
        }

    };

}
