package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoDenyBankIdRequestRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class DenyBankIdRequestTask extends ExtendedTask<Void> {

    private String requestId;
    private boolean blockMerchant;

    public DenyBankIdRequestTask(OnCompleteResultListener<Void> mListener, String requestId, boolean blockMerchant) {
        super(mListener);
        this.requestId = requestId;
        this.blockMerchant = blockMerchant;
    }

    @Override
    protected void start() {
        DtoDenyBankIdRequestRequest request = new DtoDenyBankIdRequestRequest();
        request.setRequestId(requestId);
        request.setBlockMerchant(blockMerchant);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, request, Cmd.DENY_BANK_ID_REQUEST, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<Void>> listener = new NetworkListener<Void>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> result = new Result<>();
            prepareResult(result, response);
            sendCompletition(result);
        }

    };
}
