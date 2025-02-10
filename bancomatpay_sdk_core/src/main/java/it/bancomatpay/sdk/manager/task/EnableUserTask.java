package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoEnableUserRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoEnableUserResponse;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class EnableUserTask extends ExtendedTask<Void> {

    private String phonePrefix;
    private String phoneNumber;
    private String iban;

    public EnableUserTask(OnCompleteResultListener<Void> mListener, String phonePrefix, String phoneNumber, String iban) {
        super(mListener);
        this.phonePrefix = phonePrefix;
        this.phoneNumber = phoneNumber;
        this.iban = iban;
    }

    @Override
    protected void start() {
        DtoEnableUserRequest req = new DtoEnableUserRequest();
        req.setMsisdn(phonePrefix + phoneNumber);
        req.setIban(iban);

        Single.fromCallable(new HandleRequestInteractor<>(DtoEnableUserResponse.class, req, Cmd.ENABLE_USER, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoEnableUserResponse>> l = new NetworkListener<DtoEnableUserResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoEnableUserResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                BancomatDataManager.getInstance().putUserAccountId(response.getRes().getUserAccountId());
            }
            sendCompletition(r);
        }

    };
}

