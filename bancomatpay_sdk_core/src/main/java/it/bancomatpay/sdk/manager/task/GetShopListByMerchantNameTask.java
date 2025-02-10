package it.bancomatpay.sdk.manager.task;

import android.os.AsyncTask;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetShopListByMerchantNameRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetShopListByMerchantNameResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class GetShopListByMerchantNameTask extends CancelableTask<ArrayList<ShopItem>> {

    private String name;
    private BcmLocation location;

    private AsyncTask interactor;

    public GetShopListByMerchantNameTask(OnCompleteResultListener<ArrayList<ShopItem>> mListener, String name, BcmLocation location) {
        super(mListener);
        this.name = name;
        this.location = location;
    }

    @Override
    protected void start() {
        if (name.length() < 3) {
            Result<ArrayList<ShopItem>> r = new Result<>();
            r.setStatusCode(StatusCode.Mobile.GENERIC_ERROR);
            sendCompletition(r);
        } else {
            DtoGetShopListByMerchantNameRequest dtoGetShopListByMerchantNameRequest = new DtoGetShopListByMerchantNameRequest();
            dtoGetShopListByMerchantNameRequest.setMerchantName(name);
            if (location != null) {
                dtoGetShopListByMerchantNameRequest.setLatitude(location.getLatitude());
                dtoGetShopListByMerchantNameRequest.setLongitude(location.getLongitude());
            }
            Single.fromCallable(new HandleRequestInteractor<>(DtoGetShopListByMerchantNameResponse.class, dtoGetShopListByMerchantNameRequest, Cmd.GET_SHOP_LIST_BY_MERCHANT_NAME, getJsessionClient()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ObserverSingleCustom<>(listener));
        }
    }

    @Override
    protected void cancel() {
        if (interactor != null) {
            interactor.cancel(true);
        }
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetShopListByMerchantNameResponse>> listener = new NetworkListener<DtoGetShopListByMerchantNameResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetShopListByMerchantNameResponse> response) {
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
