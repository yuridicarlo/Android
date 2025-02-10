package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoConfirmDirectDebitRequestRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class ConfirmDirectDebitRequestTask extends ExtendedTask<Void> {

    private String requestId;
    private String tag;
    private String authorizationToken;

    public ConfirmDirectDebitRequestTask(OnCompleteResultListener<Void> mListener, String requestId, String tag, String authorizationToken) {
        super(mListener);
        this.requestId = requestId;
        this.tag = tag;
        this.authorizationToken = authorizationToken;
    }

    @Override
    protected void start() {
        DtoConfirmDirectDebitRequestRequest req = new DtoConfirmDirectDebitRequestRequest();
        req.setRequestId(requestId);
        req.setTag(tag);
        req.setAuthorizationToken(authorizationToken);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.CONFIRM_DIRECT_DEBIT_REQUEST, getJsessionClient()))
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
