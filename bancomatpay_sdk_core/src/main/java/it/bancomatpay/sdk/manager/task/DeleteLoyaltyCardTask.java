package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoDeleteLoyaltyCardRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class DeleteLoyaltyCardTask extends ExtendedTask<Void> {

    private String loyaltyCardId;
    private String barCodeNumber;

    public DeleteLoyaltyCardTask(OnCompleteResultListener<Void> mListener, String barCodeNumber, String loyaltyCardId) {
        super(mListener);
        this.barCodeNumber = barCodeNumber;
        this.loyaltyCardId = loyaltyCardId;
    }

    @Override
    protected void start() {

        DtoDeleteLoyaltyCardRequest req = new DtoDeleteLoyaltyCardRequest();
        req.setBarCodeNumber(barCodeNumber);
        req.setLoyaltyCardId(loyaltyCardId);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.DELETE_LOYALTY_CARD, getJsessionClient()))
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
