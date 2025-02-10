package it.bancomatpay.sdk.manager.task;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSetBankIdContactsRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class SetBankIdContactsTask extends ExtendedTask<Void> {

    private String email;
    private List<BankIdAddress> bankIdAddresses;

    public SetBankIdContactsTask(OnCompleteResultListener<Void> mListener, String email, List<BankIdAddress> bankIdAddresses) {
        super(mListener);
        this.email = email;
        this.bankIdAddresses = bankIdAddresses;
    }

    @Override
    protected void start() {
        DtoSetBankIdContactsRequest req = new DtoSetBankIdContactsRequest();
        req.setEmail(this.email);
        req.setDtoBankIdAddresses(Mapper.getBankIdDtoAddresses(this.bankIdAddresses));

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.SET_BANK_ID_CONTACTS, getJsessionClient()))
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
