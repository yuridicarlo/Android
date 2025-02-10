package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetQrCodeDetailsRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetQrCodeDetailsResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetQrCodeDetailsTask extends ExtendedTask<QrCodeDetailsData> {

    private String qrCodeId;

    public GetQrCodeDetailsTask(OnCompleteResultListener<QrCodeDetailsData> mListener, String qrCodeId) {
        super(mListener);
        this.qrCodeId = qrCodeId;
    }

    @Override
    protected void start() {
        DtoGetQrCodeDetailsRequest dtoGetQrCodeDetailsRequest = new DtoGetQrCodeDetailsRequest();
        dtoGetQrCodeDetailsRequest.setQrCode(qrCodeId);

        Single.fromCallable(new HandleRequestInteractor<>(DtoGetQrCodeDetailsResponse.class, dtoGetQrCodeDetailsRequest, Cmd.GET_QR_CODE_DETAILS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetQrCodeDetailsResponse>> listener = new NetworkListener<DtoGetQrCodeDetailsResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetQrCodeDetailsResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<QrCodeDetailsData> r = new Result<>();
            prepareResult(r, response);

            if (r.isSuccess()) {
                QrCodeDetailsData qrCodeDetailsData = new QrCodeDetailsData();
                if (response.getRes().getDtoPayment() != null) {
                    qrCodeDetailsData.setPaymentItem(Mapper.getPaymentItem(response.getRes().getDtoPayment()));
                    qrCodeDetailsData.getPaymentItem().setQrCodeId(qrCodeId);
                } else if (response.getRes().getDtoShop() != null) {
                    qrCodeDetailsData.setShopItem(Mapper.getShopItem(response.getRes().getDtoShop()));
                }
                r.setResult(qrCodeDetailsData);
            }
            sendCompletition(r);
        }

    };

}
