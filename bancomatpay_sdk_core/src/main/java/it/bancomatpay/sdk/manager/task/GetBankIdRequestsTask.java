package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetBankIdRequestsResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.BankIdRequestsData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetBankIdRequestsTask extends ExtendedTask<BankIdRequestsData>{

    public GetBankIdRequestsTask(OnCompleteResultListener<BankIdRequestsData> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetBankIdRequestsResponse>(DtoGetBankIdRequestsResponse.class, null, Cmd.GET_BANK_ID_REQUESTS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetBankIdRequestsResponse>> listener = new NetworkListener<DtoGetBankIdRequestsResponse>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<DtoGetBankIdRequestsResponse> response) {

            CustomLogger.d(TAG, response.toString());

            Result<BankIdRequestsData> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess()) {
                result.setResult(Mapper.getBankIdRequestsData(response.getRes()));
            }

            sendCompletition(result);
        }

    };
    }




