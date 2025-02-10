package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoConfirmAtmWithdrawalRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoConfirmAtmWithdrawalResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.AtmConfirmWithdrawalData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class AtmConfirmWithdrawalTask extends ExtendedTask<AtmConfirmWithdrawalData> {

    private String msisdn;
    private String tag;
    private long shopId;
    private String amount;
    private String authorizationToken;
    private String qrCodeId;

    public AtmConfirmWithdrawalTask(OnCompleteResultListener<AtmConfirmWithdrawalData> mListener, String msisdn, String tag, long shopId, String amount, String authorizationToken, String qrCodeId) {
        super(mListener);
        this.msisdn = msisdn;
        this.tag = tag;
        this.shopId = shopId;
        this.amount = amount;
        this.authorizationToken = authorizationToken;
        this.qrCodeId = qrCodeId;
    }

    @Override
    protected void start() {
        DtoConfirmAtmWithdrawalRequest request = new DtoConfirmAtmWithdrawalRequest();
        request.setMsisdn(msisdn);
        request.setTag(tag);
        request.setShopId(shopId);
        request.setAmount(amount);
        request.setAuthorizationToken(authorizationToken);
        request.setQrCodeId(qrCodeId);

        Single.fromCallable(new HandleRequestInteractor<>(DtoConfirmAtmWithdrawalResponse.class, request, Cmd.CONFIRM_ATM_WITHDRAWAL, getJsessionClient()))
				.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    OnNetworkCompleteListener<DtoAppResponse<DtoConfirmAtmWithdrawalResponse>> listener = new NetworkListener<DtoConfirmAtmWithdrawalResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoConfirmAtmWithdrawalResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<AtmConfirmWithdrawalData> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess()) {
                result.setResult(Mapper.getAtmConfirmWithdrawalData(response.getRes()));
            }

            sendCompletition(result);
        }

    };

}
