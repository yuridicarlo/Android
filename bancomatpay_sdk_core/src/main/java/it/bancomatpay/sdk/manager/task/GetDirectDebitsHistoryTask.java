package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetDirectDebitsHistoryResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.DirectDebitsHistoryData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetDirectDebitsHistoryTask extends ExtendedTask<DirectDebitsHistoryData> {

	public GetDirectDebitsHistoryTask(OnCompleteResultListener<DirectDebitsHistoryData> mListener) {
		super(mListener);
	}

	@Override
	protected void start() {
		Single.fromCallable(new HandleRequestInteractor<Void, DtoGetDirectDebitsHistoryResponse>(DtoGetDirectDebitsHistoryResponse.class, null, Cmd.GET_DIRECT_DEBITS_HISTORY, getJsessionClient()))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new ObserverSingleCustom<>(listener));
	}

	private final OnNetworkCompleteListener<DtoAppResponse<DtoGetDirectDebitsHistoryResponse>> listener = new NetworkListener<DtoGetDirectDebitsHistoryResponse>(this) {
		@Override
		protected void manageComplete(DtoAppResponse<DtoGetDirectDebitsHistoryResponse> response) {
			CustomLogger.d(TAG, response.toString());
			Result<DirectDebitsHistoryData> result = new Result<>();
			prepareResult(result, response);

			if (result.isSuccess()) {
				if (response.getRes() != null) {
					DirectDebitsHistoryData historyData = Mapper.getDirectDebitsData(response.getRes().getDtoGetDirectDebitsHistoryElementList());
					result.setResult(historyData);
				}
			}
			sendCompletition(result);
		}
	};

}



