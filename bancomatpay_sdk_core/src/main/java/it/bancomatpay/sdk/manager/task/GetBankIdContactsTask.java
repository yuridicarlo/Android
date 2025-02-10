package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetBankIdContactsResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.BankIdContactsData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetBankIdContactsTask extends ExtendedTask<BankIdContactsData> {

    public GetBankIdContactsTask(OnCompleteResultListener<BankIdContactsData> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetBankIdContactsResponse>(DtoGetBankIdContactsResponse.class, null, Cmd.GET_BANK_ID_CONTACTS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetBankIdContactsResponse>> listener = new NetworkListener<DtoGetBankIdContactsResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetBankIdContactsResponse> response) {
            CustomLogger.d(TAG, response.toString());

            Result<BankIdContactsData> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess()) {
                result.setResult(Mapper.getBankIdContactsData(response.getRes()));
            }

            sendCompletition(result);
        }

    };

}
