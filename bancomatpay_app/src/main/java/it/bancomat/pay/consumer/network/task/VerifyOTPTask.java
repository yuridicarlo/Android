package it.bancomat.pay.consumer.network.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.dto.OtpData;
import it.bancomat.pay.consumer.network.dto.request.DtoVerifyOTPRequest;
import it.bancomat.pay.consumer.network.dto.response.DtoVerifyOTPResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppMapper;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class VerifyOTPTask extends ExtendedTask<OtpData> {

    private String otp;

    public VerifyOTPTask(OnCompleteResultListener<OtpData> mListener, String otp) {
        super(mListener);
        this.otp = otp;
    }

    @Override
    protected void start() {
        DtoVerifyOTPRequest req = new DtoVerifyOTPRequest();
        req.setOtp(otp);

        Single.fromCallable(new AppHandleRequestInteractor<>(DtoVerifyOTPResponse.class, req, AppCmd.VERIFY_OTP, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    OnNetworkCompleteListener<DtoAppResponse<DtoVerifyOTPResponse>> l = new NetworkListener<DtoVerifyOTPResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoVerifyOTPResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<OtpData> r = new Result<>();
            prepareResult(r, response);
            if(r.isSuccess()){
                OtpData otpData = new OtpData();
                otpData.setBankUuidList(AppMapper.getBankIdList(response.getRes().getDtoBankDatas()));
                otpData.setBankDataList(AppMapper.getBankDataMultiIban(response.getRes().getDtoBankDatas()));

                otpData.setToken(response.getRes().getToken());
                AppBancomatDataManager.getInstance().putActivationToken(response.getRes().getToken());
                r.setResult(otpData);
            }
            sendCompletition(r);
        }

    };

}
