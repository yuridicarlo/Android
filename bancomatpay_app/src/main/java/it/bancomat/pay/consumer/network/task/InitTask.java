package it.bancomat.pay.consumer.network.task;

import android.text.TextUtils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.response.DtoInitResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.model.AppMobileDevice;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class InitTask extends ExtendedTask<Void> {

    public InitTask(OnCompleteResultListener<Void> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        AppMobileDevice mobileDevice = AppBancomatDataManager.getInstance().getMobileDevice();
        if (mobileDevice != null && !TextUtils.isEmpty(mobileDevice.getKey()) && !TextUtils.isEmpty(mobileDevice.getUuid())) {
            CustomLogger.d(TAG, "Init NOT started");
            Result<Void> r = new Result<>();
            r.setStatusCode(StatusCode.Mobile.OK);
            sendCompletition(r);
        } else {
            if (mobileDevice == null || TextUtils.isEmpty(mobileDevice.getUuid())) {
                CustomLogger.d(TAG, "Init started");
                AppBancomatDataManager.getInstance().generateUuid();
            }

            Single.fromCallable(new AppHandleRequestInteractor<Void, DtoInitResponse>( DtoInitResponse.class, null, AppCmd.INIT, getJsessionClient()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ObserverSingleCustom<>(listener));
        }
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoInitResponse>> listener = new NetworkListener<DtoInitResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoInitResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                AppBancomatDataManager.getInstance().putMobileDeviceData(response.getRes().getKey());
            }
            sendCompletition(r);
        }

    };

}