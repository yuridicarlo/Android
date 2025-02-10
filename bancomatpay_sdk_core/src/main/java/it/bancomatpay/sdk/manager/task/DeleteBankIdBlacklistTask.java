package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.DtoBankIdMerchant;
import it.bancomatpay.sdk.manager.network.dto.request.DtoDeleteBankIdBlacklistRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class DeleteBankIdBlacklistTask extends ExtendedTask<Void> {

    private String merchantTag;
    private String merchantName;

    public DeleteBankIdBlacklistTask(OnCompleteResultListener<Void> mListener, String merchantTag, String merchantName) {
        super(mListener);
        this.merchantTag = merchantTag;
        this.merchantName = merchantName;
    }

    @Override
    protected void start() {

        DtoDeleteBankIdBlacklistRequest req = new DtoDeleteBankIdBlacklistRequest();
        DtoBankIdMerchant bankIdMerchant = new DtoBankIdMerchant();
        bankIdMerchant.setMerchantTag(this.merchantTag);
        bankIdMerchant.setBusinessName(this.merchantName);
        req.setDtoBankIdMerchant(bankIdMerchant);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.DELETE_BANK_ID_BLACKLIST, getJsessionClient()))
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
