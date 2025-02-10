package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetLoyaltyJwtRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetLoyaltyJwtResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.LoyaltyJwtData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetLoyaltyJwtTask extends ExtendedTask<LoyaltyJwtData> {

	private String loyaltyToken;

	public GetLoyaltyJwtTask(OnCompleteResultListener<LoyaltyJwtData> mListener, String loyaltyToken) {
		super(mListener);
		this.loyaltyToken = loyaltyToken;
	}

	@Override
	protected void start() {
		DtoGetLoyaltyJwtRequest req = new DtoGetLoyaltyJwtRequest();
		req.setLoyaltyToken(this.loyaltyToken);

		Single.fromCallable(new HandleRequestInteractor<>(DtoGetLoyaltyJwtResponse.class, req, Cmd.GET_LOYALTY_JWT, getJsessionClient()))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new ObserverSingleCustom<>(listener));
	}

	private OnNetworkCompleteListener<DtoAppResponse<DtoGetLoyaltyJwtResponse>> listener = new NetworkListener<DtoGetLoyaltyJwtResponse>(this) {

		@Override
		protected void manageComplete(DtoAppResponse<DtoGetLoyaltyJwtResponse> response) {
			CustomLogger.d(TAG, response.toString());
			Result<LoyaltyJwtData> r = new Result<>();
			prepareResult(r, response);
			if (r.isSuccess()) {
				LoyaltyJwtData loyaltyJwtData = Mapper.getLoyaltyJwtData(response.getRes());
				r.setResult(loyaltyJwtData);
			}
			sendCompletition(r);
		}

	};

}

