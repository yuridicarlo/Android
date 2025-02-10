package it.bancomat.pay.consumer.network.task;

import android.text.TextUtils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkRefreshListener;
import it.bancomat.pay.consumer.network.dto.request.DtoPushRegistrationRequest;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class PushRegistrationTask extends RefreshTokenTask<Void> {

	private String token;
	private String sessionToken;

	public PushRegistrationTask(OnCompleteResultListener<Void> mListener, String token, String sessionToken) {
		super(mListener);
		this.token = token;
		this.sessionToken = sessionToken;
	}

	@Override
	public void start() {
		if (!TextUtils.isEmpty(sessionToken)) {
			SessionManager.getInstance().setSessionToken(sessionToken);
		}

		DtoPushRegistrationRequest req = new DtoPushRegistrationRequest();
		req.setToken(token);

		Single.fromCallable(new AppHandleRequestInteractor<>(Void.class, req, AppCmd.PUSH_REGISTRATION, getJsessionClient()))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new ObserverSingleCustom<>(listener));
	}

	private OnNetworkCompleteListener<DtoAppResponse<Void>> listener = new NetworkRefreshListener<Void>(this) {
		@Override
		protected void manageComplete(DtoAppResponse<Void> response) {
			CustomLogger.d(TAG, response.toString());
			Result<Void> r = new Result<>();
			prepareResult(r, response);
			sendCompletition(r);
		}
	};

}
