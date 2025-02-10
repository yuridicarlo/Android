package it.bancomatpay.sdk.manager.task;

import android.os.Handler;
import android.os.Looper;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.PaymentStateType;
import it.bancomatpay.sdk.manager.network.dto.request.DtoVerifyPaymentStateRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoVerifyPaymentStateResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.PaymentData;
import it.bancomatpay.sdk.manager.task.model.PollingData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class VerifyPaymentStateTask extends ExtendedTask<PaymentData> {

    private String paymentId;
    private Handler handler;
    private Handler handlerRetry;
    private PollingData pollingData;

    public VerifyPaymentStateTask(OnCompleteResultListener<PaymentData> mListener, String paymentId, PollingData pollingData) {
        super(mListener);
        this.paymentId = paymentId;
        this.pollingData = pollingData;
    }

    @Override
    protected void start() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                if (handlerRetry != null) {
                    handlerRetry.removeCallbacksAndMessages(null);
                }
                Result<PaymentData> r = new Result<>();
                PaymentData paymentData = new PaymentData();
                paymentData.setState(PaymentData.State.FAILED);
                r.setStatusCode(StatusCode.Mobile.OK);
                r.setResult(paymentData);
                sendCompletition(r);
            }, pollingData.getPollingMaxTimeSeconds());
        }

        DtoVerifyPaymentStateRequest dtoVerifyPaymentStateRequest = new DtoVerifyPaymentStateRequest();
        dtoVerifyPaymentStateRequest.setPaymentId(paymentId);

        Single.fromCallable(new HandleRequestInteractor<>(DtoVerifyPaymentStateResponse.class, dtoVerifyPaymentStateRequest, Cmd.VERIFY_PAYMENT_STATE, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private void retryPostDelay() {
        if (handlerRetry == null) {
            handlerRetry = new Handler();
        } else {
            handlerRetry.removeCallbacksAndMessages(null);
        }
        handlerRetry.postDelayed(this::start, pollingData.getPollingRangeTimeSeconds());
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoVerifyPaymentStateResponse>> listener = new NetworkListener<DtoVerifyPaymentStateResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoVerifyPaymentStateResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<PaymentData> r = new Result<>();
            PaymentData paymentData = new PaymentData();
            r.setResult(paymentData);
            prepareResult(r, response);

            if (!r.isSuccess() || response.getRes().getPaymentState() == PaymentStateType.WAIT) {
                retryPostDelay();
            } else {
                paymentData.setState(Mapper.gePaymentState(response.getRes().getPaymentState()));
                sendCompletition(r);
            }
        }

        @Override
        public void onCompleteWithError(Error e) {
            if (!task.managedError(e)) {
                retryPostDelay();
            }
        }

    };
}
