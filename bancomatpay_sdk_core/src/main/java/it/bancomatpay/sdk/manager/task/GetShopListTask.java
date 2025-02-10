package it.bancomatpay.sdk.manager.task;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetShopListRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetShopListResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetShopListTask extends ExtendedTask<ArrayList<ShopItem>> {

    private BcmLocation location;

    GetShopListTask(OnCompleteResultListener<ArrayList<ShopItem>> mListener, BcmLocation location) {
        super(mListener);
        this.location = location;
    }

    @Override
    protected void start() {
        DtoGetShopListRequest dtoGetShopListRequest = new DtoGetShopListRequest();
        dtoGetShopListRequest.setLatitude(location.getLatitude());
        dtoGetShopListRequest.setLongitude(location.getLongitude());

        Single.fromCallable(new HandleRequestInteractor<>(DtoGetShopListResponse.class, dtoGetShopListRequest, Cmd.GET_SHOP_LIST, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetShopListResponse>> listener = new NetworkListener<DtoGetShopListResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetShopListResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<ArrayList<ShopItem>> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                r.setResult(Mapper.getShopItems(response.getRes().getShops()));
            }
            sendCompletition(r);
        }

    };

}
