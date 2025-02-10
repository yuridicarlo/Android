package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.DtoShop;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetMerchantDetailsRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetMerchantDetailsTask extends ExtendedTask<ShopItem> {

    protected String msisdn;
    protected String tag;
    protected String shopId;

    public GetMerchantDetailsTask(OnCompleteResultListener<ShopItem> mListener, String msisdn) {
        super(mListener);
        this.msisdn = msisdn;
    }

    public GetMerchantDetailsTask(OnCompleteResultListener<ShopItem> mListener, String tag, String shopId) {
        super(mListener);
        this.tag = tag;
        this.shopId = shopId;
    }

    @Override
    protected void start() {
        DtoGetMerchantDetailsRequest dtoGetMerchantDetailsRequest = new DtoGetMerchantDetailsRequest();
        dtoGetMerchantDetailsRequest.setMsisdn(msisdn);
        dtoGetMerchantDetailsRequest.setTag(tag);
        dtoGetMerchantDetailsRequest.setShopId(shopId);

        Single.fromCallable(new HandleRequestInteractor<>(DtoShop.class, dtoGetMerchantDetailsRequest, Cmd.GET_MERCHANT_DETAILS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoShop>> listener = new NetworkListener<DtoShop>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoShop> response) {
            CustomLogger.d(TAG, response.toString());
            Result<ShopItem> r = new Result<>();
            prepareResult(r, response);

            if (r.isSuccess()) {
                r.setResult(Mapper.getShopItem(response.getRes()));
            }
            sendCompletition(r);
        }

    };

}
