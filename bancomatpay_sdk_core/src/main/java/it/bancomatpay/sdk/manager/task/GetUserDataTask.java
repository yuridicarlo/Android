package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetUserDataResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetUserDataTask extends ExtendedTask<UserData> {

    GetUserDataTask(OnCompleteResultListener<UserData> mListener, String sessionToken) {
        super(mListener);
        SessionManager.getInstance().setSessionToken(sessionToken);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetUserDataResponse>(DtoGetUserDataResponse.class, null, Cmd.GET_USER_DATA, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetUserDataResponse>> listener = new NetworkListener<DtoGetUserDataResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetUserDataResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<UserData> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess() && response.getRes() != null) {
                UserData userData = Mapper.getUserData(response.getRes());
                result.setResult(userData);
            }
            sendCompletition(result);
        }
    };

}
