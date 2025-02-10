package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetDocumentImagesRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetDocumentImagesResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.DocumentImages;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetDocumentImagesTask extends ExtendedTask<DocumentImages> {

    private String documentUuid;

    public GetDocumentImagesTask(OnCompleteResultListener<DocumentImages> mListener, String documentUuid) {
        super(mListener);
        this.documentUuid = documentUuid;
    }

    @Override
    protected void start() {
        DtoGetDocumentImagesRequest req = new DtoGetDocumentImagesRequest();
        req.setDocumentUuid(documentUuid);
        Single.fromCallable(new HandleRequestInteractor<>(DtoGetDocumentImagesResponse.class, req, Cmd.GET_DOCUMENT_IMAGES, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetDocumentImagesResponse>> listener = new NetworkListener<DtoGetDocumentImagesResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetDocumentImagesResponse> response) {

            CustomLogger.d(TAG, response.toString());
            Result<DocumentImages> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess() && response.getRes() != null) {
                result.setResult(Mapper.getDocumentImages(response.getRes()));
            }

            sendCompletition(result);
        }
    };


}
