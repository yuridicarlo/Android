package it.bancomat.pay.consumer.network.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.request.DtoInitUserRequest;
import it.bancomat.pay.consumer.network.dto.response.DtoInitUserResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class InitUserTask extends ExtendedTask<Void> {

    private String msisdn;

    public InitUserTask(OnCompleteResultListener<Void> mListener, String msIsdn) {
        super(mListener);
        this.msisdn = msIsdn;
    }

    @Override
    protected void start() {
        DtoInitUserRequest req = new DtoInitUserRequest();
        req.setMsisdn(msisdn);

        Single.fromCallable(new AppHandleRequestInteractor<>(DtoInitUserResponse.class, req, AppCmd.INIT_USER, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoInitUserResponse>> l = new NetworkListener<DtoInitUserResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoInitUserResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);

            if (r.isSuccess()) {
                AppBancomatDataManager.getInstance().putUserAccountId(response.getRes().getUserAccountId());
            }
            sendCompletition(r);
        }

    };

}
