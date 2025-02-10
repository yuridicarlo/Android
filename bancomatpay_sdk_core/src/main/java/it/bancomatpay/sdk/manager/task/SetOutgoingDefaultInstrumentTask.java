package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSetOutgoingDefaultInstrumentRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SetOutgoingDefaultInstrumentTask extends ExtendedTask<Void>  {

    private String iban;

    public SetOutgoingDefaultInstrumentTask(OnCompleteResultListener<Void> mListener, String iban) {
        super(mListener);
        this.iban = iban;
    }

    @Override
    protected void start() {
        DtoSetOutgoingDefaultInstrumentRequest req = new DtoSetOutgoingDefaultInstrumentRequest();
        req.setIban(iban);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.SET_OUTGOING_DEFAULT_INSTRUMENT, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    private OnNetworkCompleteListener<DtoAppResponse<Void>> l = new NetworkListener<Void>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
//            if (r.isSuccess()) {
//                ApplicationModel.getInstance().getUserData().setDefaultReceiver(true);
//            }
            sendCompletition(r);
        }
    };

}
