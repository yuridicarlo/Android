package it.bancomatpay.sdk.manager.task;

import java.math.BigInteger;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoConfirmPosWithdrawalRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoConfirmPosWithdrawalResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.PosConfirmWithdrawalData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class ConfirmPosWithdrawalTask extends ExtendedTask<PosConfirmWithdrawalData> {

    private String tag;
    private long shopId;
    private BigInteger tillId;
    private String amount;
    private String totalAmount;
    private String authorizationToken;
    private String qrCodeId;
    private String causal;

    public ConfirmPosWithdrawalTask(OnCompleteResultListener<PosConfirmWithdrawalData> mListener, String tag, long shopId, BigInteger tillId, String amount, String totalAmount, String authorizationToken, String qrCodeId, String causal) {
        super(mListener);
        this.tag = tag;
        this.shopId = shopId;
        this.tillId = tillId;
        this.amount = amount;
        this.totalAmount = totalAmount;
        this.authorizationToken = authorizationToken;
        this.qrCodeId = qrCodeId;
        this.causal = causal;
    }

    @Override
    protected void start() {
        DtoConfirmPosWithdrawalRequest request = new DtoConfirmPosWithdrawalRequest();
        request.setTag(tag);
        request.setShopId(shopId);
        request.setTillId(tillId);
        request.setAmount(amount);
        request.setTotalAmount(totalAmount);
        request.setAuthorizationToken(authorizationToken);
        request.setQrCodeId(qrCodeId);
        request.setCausal(causal);

        Single.fromCallable(new HandleRequestInteractor<>(DtoConfirmPosWithdrawalResponse.class, request, Cmd.CONFIRM_POS_WITHDRAWAL, getJsessionClient()))
				.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    OnNetworkCompleteListener<DtoAppResponse<DtoConfirmPosWithdrawalResponse>> listener = new NetworkListener<DtoConfirmPosWithdrawalResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoConfirmPosWithdrawalResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<PosConfirmWithdrawalData> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess()) {
                result.setResult(Mapper.getPosConfirmWithdrawalData(response.getRes()));
            }

            sendCompletition(result);
        }

    };

}
