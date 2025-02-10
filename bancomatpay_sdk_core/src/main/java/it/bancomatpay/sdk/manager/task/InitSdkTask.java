package it.bancomatpay.sdk.manager.task;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.events.request.BankServicesUpdateEvent;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoInitResponse;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.MobileDevice;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class InitSdkTask extends ExtendedTask<Void> {

    private Boolean checkRoot;
    private Boolean blockIfRooted;
    private String abiCode;
    private String groupCode;

    public InitSdkTask(OnCompleteResultListener<Void> mListener, Boolean checkRoot, Boolean blockIfRooted,
                       String abiCode, String groupCode) {
        super(mListener);
        this.checkRoot = checkRoot;
        this.blockIfRooted = blockIfRooted;
        this.abiCode = abiCode;
        this.groupCode = groupCode;
    }

    @Override
    protected void start() {
        MobileDevice mobileDevice = BancomatDataManager.getInstance().getMobileDevice();
        if (mobileDevice == null || TextUtils.isEmpty(mobileDevice.getUuid())) {
            CustomLogger.d(TAG, "InitSdk started");
            BancomatDataManager.getInstance().generateUuid();
        }
        Single.fromCallable(new HandleRequestInteractor<Void, DtoInitResponse>(DtoInitResponse.class, null, Cmd.INIT, getJsessionClient(), abiCode, groupCode))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoInitResponse>> l = new NetworkListener<DtoInitResponse>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<DtoInitResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                BancomatDataManager dataManager = BancomatDataManager.getInstance();
                dataManager.putMobileDeviceData(response.getRes().getKey());
                dataManager.putAntirootCheckEnabled(checkRoot);
                dataManager.putBlockIfRooted(blockIfRooted);
                dataManager.putBankInfo(abiCode, groupCode);
                dataManager.putBankActiveServices(Mapper.getBankServiceList(response.getRes().getBankServiceList()));
                EventBus.getDefault().post(new BankServicesUpdateEvent());
            }
            sendCompletition(r);
        }
    };

}
