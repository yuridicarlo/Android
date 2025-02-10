package it.bancomat.pay.consumer.network.task;

import android.text.TextUtils;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.request.DtoGetBanksConfigurationFileRequest;
import it.bancomat.pay.consumer.network.dto.response.DtoGetBanksConfigurationFileResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.CreateBanksDataTask;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.model.VoidResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class GetConfigurationBankFileTask extends ExtendedTask<DtoGetBanksConfigurationFileResponse> {

    private String version;

    public GetConfigurationBankFileTask(OnCompleteResultListener<DtoGetBanksConfigurationFileResponse> mListener, String version) {
        super(mListener);
        this.version = version;
    }

    @Override
    protected void start() {
        DtoGetBanksConfigurationFileRequest request = new DtoGetBanksConfigurationFileRequest();
        request.setVersion(this.version);

        Single.fromCallable(new AppHandleRequestInteractor<>(DtoGetBanksConfigurationFileResponse.class, request, AppCmd.GET_BANKS_CONFIGURATION_FILE, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    OnNetworkCompleteListener<DtoAppResponse<DtoGetBanksConfigurationFileResponse>> l = new NetworkListener<DtoGetBanksConfigurationFileResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetBanksConfigurationFileResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<DtoGetBanksConfigurationFileResponse> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                r.setResult(response.getRes());
                if (!TextUtils.isEmpty(response.getRes().getFile())) {
                    Single.fromCallable(new CreateBanksDataTask(response.getRes()))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new SingleObserver<VoidResponse>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                }

                                @Override
                                public void onSuccess(@NonNull VoidResponse voidResponse) {
                                CustomLogger.d(TAG, "CreateBanksDataTask success, BanksFile saved");
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    CustomLogger.e(TAG, "CreateBanksDataTask exception: " + e.getMessage());

                                }
                            });
                }


            }
            sendCompletition(r);
        }

    };

}
