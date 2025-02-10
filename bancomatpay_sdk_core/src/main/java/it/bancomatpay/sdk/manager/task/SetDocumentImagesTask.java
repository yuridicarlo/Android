package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSetDocumentImagesRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SetDocumentImagesTask extends ExtendedTask<Void> {

    private String documentUuid;
    private String frontImage;
    private String backImage;

    public SetDocumentImagesTask(OnCompleteResultListener<Void> mListener, String documentUuid, String frontImage, String backImage) {
        super(mListener);
        this.documentUuid = documentUuid;
        this.frontImage = frontImage;
        this.backImage = backImage;
    }

    @Override
    protected void start() {
        DtoSetDocumentImagesRequest req = new DtoSetDocumentImagesRequest();
        req.setDocumentUuid(documentUuid);
        req.setFrontImage(frontImage);
        req.setBackImage(backImage);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.SET_DOCUMENT_IMAGES, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<Void>> listener = new NetworkListener<Void>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
            sendCompletition(r);
        }
    };

}
