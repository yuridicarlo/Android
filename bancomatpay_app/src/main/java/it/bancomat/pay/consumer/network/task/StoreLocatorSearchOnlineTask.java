package it.bancomat.pay.consumer.network.task;

import android.os.AsyncTask;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetShopListRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetShopListResponse;
import it.bancomatpay.sdk.manager.task.CancelableTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.ShopList;
import it.bancomatpay.sdk.manager.task.model.ShopType;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class StoreLocatorSearchOnlineTask extends CancelableTask<ShopList> {

    private String shopName;
    private String categoryUuid;
    private int page;

    private AsyncTask interactor;

    public StoreLocatorSearchOnlineTask(OnCompleteResultListener<ShopList> mListener, String shopName, String categoryUuid, int page) {
        super(mListener);
        this.shopName = shopName;
        this.categoryUuid = categoryUuid;
        this.page = page;
    }

    @Override
    protected void start() {
        AppCmd cmd;
        if(BancomatPayApiInterface.Factory.getInstance().isUserRegistered()) {
            cmd = AppCmd.GET_STORE_LOCATOR_SEARCH_REQUEST;
        } else {
            cmd = AppCmd.GET_STORE_LOCATOR_SEARCH_REQUEST_PRE;
        }

        DtoGetShopListRequest dtoGetShopListRequest = new DtoGetShopListRequest();
        dtoGetShopListRequest.setShopName(shopName);
        dtoGetShopListRequest.setNextPageToShow(page);
        dtoGetShopListRequest.setShopType(ShopType.ON_LINE);
        dtoGetShopListRequest.setCategoryUuid(categoryUuid);
//        dtoGetShopListRequest.setPageSize();

        if(shopName != null && !shopName.isEmpty()) {
            dtoGetShopListRequest.setShopName(shopName);
        }

        Single.fromCallable(new AppHandleRequestInteractor<>(DtoGetShopListResponse.class, dtoGetShopListRequest, cmd, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    @Override
    protected void cancel() {
        if (interactor != null) {
            interactor.cancel(true);
        }
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetShopListResponse>> listener = new NetworkListener<DtoGetShopListResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetShopListResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<ShopList> r = new Result<>();

            prepareResult(r, response);
            if (r.isSuccess()) {
                ArrayList<ShopItem> shops = Mapper.getShopItems(response.getRes().getShops());
                ShopList shopList = new ShopList();
                shopList.setShops(shops);
                shopList.setNextPageToShow(response.getRes().getNextPageToShow());
                shopList.setListComplete(response.getRes().isListComplete());
                r.setResult(shopList);
            }
            sendCompletition(r);
        }

    };

}