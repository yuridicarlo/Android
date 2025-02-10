package it.bancomatpay.sdk.manager.task;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetBankIdBlacklistResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.BankIdMerchantData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetBankIdBlacklistTask extends ExtendedTask<ArrayList<BankIdMerchantData>> {

    public GetBankIdBlacklistTask(OnCompleteResultListener<ArrayList<BankIdMerchantData>> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetBankIdBlacklistResponse>(DtoGetBankIdBlacklistResponse.class, null, Cmd.GET_BANK_ID_BLACKLIST, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetBankIdBlacklistResponse>> listener = new NetworkListener<DtoGetBankIdBlacklistResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetBankIdBlacklistResponse> response) {
            CustomLogger.d(TAG, response.toString());

            Result<ArrayList<BankIdMerchantData>> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess()) {
                result.setResult(Mapper.getBankIdMerchantDataList(response.getRes()));
            }

            sendCompletition(result);
        }

    };

}
