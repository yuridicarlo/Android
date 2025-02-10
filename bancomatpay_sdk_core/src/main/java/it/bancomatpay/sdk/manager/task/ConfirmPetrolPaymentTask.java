package it.bancomatpay.sdk.manager.task;

import java.math.BigInteger;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoConfirmPetrolPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoConfirmPetrolPaymentResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ConfirmPetrolPaymentData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class ConfirmPetrolPaymentTask extends ExtendedTask<ConfirmPetrolPaymentData> {

	private String tag;
	private String shopId;
	private BigInteger tillId;
	private String amount;
	private String authorizationToken;

	public ConfirmPetrolPaymentTask(OnCompleteResultListener<ConfirmPetrolPaymentData> mListener, String tag, String shopId, BigInteger tillId, String amount, String authorizationToken) {
		super(mListener);
		this.tag = tag;
		this.shopId = shopId;
		this.tillId = tillId;
		this.amount = amount;
		this.authorizationToken = authorizationToken;
	}

	@Override
	protected void start() {
		DtoConfirmPetrolPaymentRequest req = new DtoConfirmPetrolPaymentRequest();
		req.setAmount(this.amount);
		req.setAuthorizationToken(this.authorizationToken);
		req.setShopId(this.shopId);
		req.setTag(this.tag);
		req.setTillId(this.tillId);

		Single.fromCallable(new HandleRequestInteractor<>(DtoConfirmPetrolPaymentResponse.class, req, Cmd.CONFIRM_PETROL_PAYMENT, getJsessionClient()))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new ObserverSingleCustom<>(listener));
	}

	private OnNetworkCompleteListener<DtoAppResponse<DtoConfirmPetrolPaymentResponse>> listener = new NetworkListener<DtoConfirmPetrolPaymentResponse>(this) {

		@Override
		protected void manageComplete(DtoAppResponse<DtoConfirmPetrolPaymentResponse> response) {
			CustomLogger.d(TAG, response.toString());
			Result<ConfirmPetrolPaymentData> r = new Result<>();
			prepareResult(r, response);
			if (r.isSuccess()) {
				ConfirmPetrolPaymentData confirmPetrolPaymentData = Mapper.getConfirmPetrolPaymentData(response.getRes());
				r.setResult(confirmPetrolPaymentData);
			}
			sendCompletition(r);
		}

	};

}
