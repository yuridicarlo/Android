package it.bancomat.pay.consumer.network.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkRefreshListener;
import it.bancomat.pay.consumer.network.dto.request.DtoSetCustomerJourneyConsentsRequest;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.DtoCustomerJourneyConsents;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SetCustomerJourneyConsentsTask extends RefreshTokenTask<Void> {

	private boolean allowProfiling;
	private boolean allowMarketing;
	private boolean allowDataToThirdParties;

	public SetCustomerJourneyConsentsTask(OnCompleteResultListener<Void> mListener, boolean allowProfiling, boolean allowMarketing, boolean allowDataToThirdParties) {
		super(mListener);
		this.allowProfiling = allowProfiling;
		this.allowMarketing = allowMarketing;
		this.allowDataToThirdParties = allowDataToThirdParties;
	}

	@Override
	protected void start() {
		DtoSetCustomerJourneyConsentsRequest req = new DtoSetCustomerJourneyConsentsRequest();
		DtoCustomerJourneyConsents consents = new DtoCustomerJourneyConsents();
		consents.setProfiling(allowProfiling);
		consents.setMarketing(allowMarketing);
		consents.setDataToThirdParties(allowDataToThirdParties);
		req.setConsents(consents);

		Single.fromCallable(new AppHandleRequestInteractor<>( Void.class, req, AppCmd.SET_CUSTOMER_JOURNEY_CONSENTS, getJsessionClient()))
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

