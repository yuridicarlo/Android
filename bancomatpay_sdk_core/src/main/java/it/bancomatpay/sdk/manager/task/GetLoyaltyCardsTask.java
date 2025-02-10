package it.bancomatpay.sdk.manager.task;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetLoyaltyCardsResponse;
import it.bancomatpay.sdk.manager.task.interactor.FrequentLoyaltyCardInteractor;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardsData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class GetLoyaltyCardsTask extends ExtendedTask<LoyaltyCardsData> {

    private Result<LoyaltyCardsData> result;

    public GetLoyaltyCardsTask(OnCompleteResultListener<LoyaltyCardsData> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {

        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetLoyaltyCardsResponse>(DtoGetLoyaltyCardsResponse.class, null, Cmd.GET_LOYALTY_CARDS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetLoyaltyCardsResponse>> listener = new NetworkListener<DtoGetLoyaltyCardsResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetLoyaltyCardsResponse> response) {
            CustomLogger.d(TAG, response.toString());
            result = new Result<>();
            prepareResult(result, response);
            if (result.isSuccess()) {
                LoyaltyCardsData resultData = Mapper.getLoyaltyCardsData(response.getRes());

                Single.fromCallable(new FrequentLoyaltyCardInteractor())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new SingleObserver<HashMap<String, Integer>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@NonNull HashMap<String, Integer> stringIntegerHashMap) {
                                LoyaltyCardsData newCardData = new LoyaltyCardsData();
                                newCardData.setLoyaltyCardList(new ArrayList<>());
                                if (stringIntegerHashMap != null) {
                                    for (LoyaltyCard item : resultData.getLoyaltyCardList()) {
                                        if (stringIntegerHashMap.containsKey(item.getLoyaltyCardId())) {
                                            int operationCounter = stringIntegerHashMap.get(item.getLoyaltyCardId()) != null ? stringIntegerHashMap.get(item.getLoyaltyCardId()) : 0;
                                            item.setOperationCounter(operationCounter);
                                            newCardData.getLoyaltyCardList().add(item);
                                        } else {
                                            newCardData.getLoyaltyCardList().add(item);
                                        }
                                    }
                                }

                                GetLoyaltyCardsTask.this.result.setResult(newCardData);
                                sendCompletition(GetLoyaltyCardsTask.this.result);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
							Result<LoyaltyCardsData> result = new Result<>();
							result.setStatusCode(StatusCode.Mobile.GENERIC_ERROR);
							sendCompletition(result);
							CustomLogger.e(TAG, "Error in FrequentLoyaltyCardInteractor: " + e.getMessage());
                            }
                        });


            }
        }

    };

}
