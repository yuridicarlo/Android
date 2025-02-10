package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetDocumentsResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.DocumentsData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetDocumentsTask extends ExtendedTask<DocumentsData> {

    public GetDocumentsTask(OnCompleteResultListener<DocumentsData> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {

        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetDocumentsResponse>(DtoGetDocumentsResponse.class, null, Cmd.GET_DOCUMENTS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetDocumentsResponse>> listener = new NetworkListener<DtoGetDocumentsResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetDocumentsResponse> response) {

            CustomLogger.d(TAG, response.toString());
            Result<DocumentsData> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess()) {
                if (response.getRes() != null) {
                    DocumentsData userData = Mapper.getDocumentsData(response.getRes().getDtoDocuments());
                    result.setResult(userData);
                }
                sendCompletition(result);
            } else {
                sendCompletition(result);
            }
        }
    };

}
