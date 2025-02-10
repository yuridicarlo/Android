package it.bancomat.pay.consumer.network.task;

import android.text.TextUtils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.request.DtoSetPinRequest;
import it.bancomat.pay.consumer.network.dto.response.DtoSetPinResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.network.totp.PSKCMapper;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.model.Pskc;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SetPinTask extends ExtendedTask<DtoSetPinResponse> {

    private String pin, token, outgoingIban;

    public SetPinTask(OnCompleteResultListener<DtoSetPinResponse> mListener, String pin, String token, String outgoingIban) {
        super(mListener);
        this.pin = pin;
        this.token = token;
        this.outgoingIban = outgoingIban;
    }

    @Override
    protected void start() {
        DtoSetPinRequest req = new DtoSetPinRequest();
        req.setPin(pin);
        req.setToken(token);
        if (!TextUtils.isEmpty(outgoingIban)) {
            req.setOutgoingIban(outgoingIban);
        }

        Single.fromCallable(new AppHandleRequestInteractor<>(DtoSetPinResponse.class, req, AppCmd.SET_PIN, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoSetPinResponse>> l = new NetworkListener<DtoSetPinResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoSetPinResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<DtoSetPinResponse> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                AppBancomatDataManager.getInstance().putTokens(
                        response.getRes().getTokens().getAuthorizationToken().getToken(),
                        response.getRes().getTokens().getRefreshToken().getToken());
                Pskc pskc = PSKCMapper.buildPskcV2(response.getRes().getPskc());
                AppBancomatDataManager.getInstance().putPskc(pskc);

                boolean outgoingIbanSet = response.getRes().isOutgoingIbanSet();
                CustomLogger.d(TAG, "SetPinTask - outgoingIbanSet = " + outgoingIbanSet);

                r.setResult(response.getRes());
            }
            sendCompletition(r);
        }

    };
}
