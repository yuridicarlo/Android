package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoDenyDirectDebitRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class DenyDirectDebitsRequestTask extends ExtendedTask<Void> {

    private String requestId;
    private String tag;

    public DenyDirectDebitsRequestTask(OnCompleteResultListener<Void> mListener, String requestId, String tag) {
        super(mListener);
        this.requestId = requestId;
        this.tag = tag;
    }

    @Override
    protected void start() {
        DtoDenyDirectDebitRequest request = new DtoDenyDirectDebitRequest();
        request.setRequestId(requestId);
        request.setTag(tag);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, request, Cmd.DENY_DIRECT_DEBIT_REQUEST, getJsessionClient()))
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
