package it.bancomat.pay.consumer.network.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.VerifyActionCodeData;
import it.bancomat.pay.consumer.network.dto.request.DtoVerifyActivationCodeRequest;
import it.bancomat.pay.consumer.network.dto.response.DtoVerifyActivationCodeResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class VerifyActivationCodeTask extends ExtendedTask<VerifyActionCodeData> {

    private String activationCode, bankUuid;

    public VerifyActivationCodeTask(OnCompleteResultListener<VerifyActionCodeData> mListener, String activationCode, String bankUuid) {
        super(mListener);
        this.activationCode = activationCode;
        this.bankUuid = bankUuid;
    }

    @Override
    protected void start() {
        DtoVerifyActivationCodeRequest req = new DtoVerifyActivationCodeRequest();
        req.setActivationCode(activationCode);
        req.setBankUUID(bankUuid);

        Single.fromCallable(new AppHandleRequestInteractor<>(DtoVerifyActivationCodeResponse.class, req, AppCmd.VERIFY_ACTIVATION_CODE, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoVerifyActivationCodeResponse>> l = new NetworkListener<DtoVerifyActivationCodeResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoVerifyActivationCodeResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<VerifyActionCodeData> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                VerifyActionCodeData verifyActionCodeData = new VerifyActionCodeData();
                verifyActionCodeData.setBankUUID(response.getRes().getBankUUID());
                verifyActionCodeData.setToken(response.getRes().getToken());
                r.setResult(verifyActionCodeData);
            }
            sendCompletition(r);
        }

    };

}