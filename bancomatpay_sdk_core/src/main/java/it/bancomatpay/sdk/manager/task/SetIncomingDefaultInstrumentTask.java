package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSetIncomingDefaultInstrumentRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SetIncomingDefaultInstrumentTask extends ExtendedTask<Void> {

    private String iban;

    public SetIncomingDefaultInstrumentTask(OnCompleteResultListener<Void> mListener, String iban) {
        super(mListener);
        this.iban = iban;
    }

    @Override
    protected void start() {
        DtoSetIncomingDefaultInstrumentRequest req = new DtoSetIncomingDefaultInstrumentRequest();
        req.setIban(iban);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.SET_INCOMING_DEFAULT_INSTRUMENT, getJsessionClient()))
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
            if (r.isSuccess()) {
                ApplicationModel.getInstance().getUserData().setDefaultReceiver(true);
            }
            sendCompletition(r);
        }
    };

}
