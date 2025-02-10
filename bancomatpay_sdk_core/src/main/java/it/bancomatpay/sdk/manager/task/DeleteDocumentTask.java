package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoDeleteDocumentRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class DeleteDocumentTask extends ExtendedTask<Void> {

    private String documentUuid;

    public DeleteDocumentTask(OnCompleteResultListener<Void> mListener, String documentUuid) {
        super(mListener);
        this.documentUuid = documentUuid;
    }

    @Override
    protected void start() {
        DtoDeleteDocumentRequest req = new DtoDeleteDocumentRequest();
        req.setDocumentUuid(documentUuid);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.DELETE_DOCUMENT, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    OnNetworkCompleteListener<DtoAppResponse<Void>> l = new NetworkListener<Void>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
            sendCompletition(r);
        }
    };

}
