package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetLoyaltyCardBrandsResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardBrandsData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetLoyaltyCardBrandsTask extends ExtendedTask<LoyaltyCardBrandsData> {

    public GetLoyaltyCardBrandsTask(OnCompleteResultListener<LoyaltyCardBrandsData> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetLoyaltyCardBrandsResponse>(DtoGetLoyaltyCardBrandsResponse.class, null, Cmd.GET_LOYALTY_CARD_BRANDS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    OnNetworkCompleteListener<DtoAppResponse<DtoGetLoyaltyCardBrandsResponse>> l = new NetworkListener<DtoGetLoyaltyCardBrandsResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetLoyaltyCardBrandsResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<LoyaltyCardBrandsData> result = new Result<>();
            prepareResult(result, response);
            if (result.isSuccess()) {
                result.setResult(Mapper.getLoyaltyCardBrandsData(response.getRes()));
            }
            sendCompletition(result);
        }

    };

}
