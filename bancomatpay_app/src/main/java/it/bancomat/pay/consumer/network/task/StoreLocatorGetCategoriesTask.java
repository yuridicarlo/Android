package it.bancomat.pay.consumer.network.task;

import android.content.Context;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetStoreLocatorMccResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.utilities.MapperConsumer;

public class StoreLocatorGetCategoriesTask extends ExtendedTask<List<ShopCategory>> {

    public StoreLocatorGetCategoriesTask(OnCompleteResultListener<List<ShopCategory>> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        AppCmd cmd;
        if(BancomatPayApiInterface.Factory.getInstance().isUserRegistered()) {
            cmd = AppCmd.GET_STORE_LOCATOR_MCC;
        } else {
            cmd = AppCmd.GET_STORE_LOCATOR_MCC_PRE;
        }

        Single.fromCallable(new AppHandleRequestInteractor<>(DtoGetStoreLocatorMccResponse.class, null, cmd, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetStoreLocatorMccResponse>> listener = new NetworkListener<DtoGetStoreLocatorMccResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetStoreLocatorMccResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<List<ShopCategory>> r = new Result<>();


            prepareResult(r, response);
            if (r.isSuccess()) {
                List<ShopCategory> categories = MapperConsumer.getShopCategoriesFromDtoShopCategories(response.getRes().getCategories());

                //title localization
                Context context = PayCore.getAppContext();
                for (ShopCategory item : categories) {
                    switch (item.getUuid()) {
                        case "VAGL2IVHBV0KQGA3MXST" :
                            item.setTitle(context.getString(R.string.category_clothing));
                            break;
                        case "5I9ETKM7UAGPY8V1EQSV" :
                            item.setTitle(context.getString(R.string.category_hotels));
                            break;
                        case "A0I47K0R4X7KKYVKGXWX" :
                            item.setTitle(context.getString(R.string.category_cinema_and_entertaiment));
                            break;
                        case "SALPCTN89F1PN84KR5MP" :
                            item.setTitle(context.getString(R.string.category_home));
                            break;
                        case "K62CABL3Y0NV79H9HIXQ" :
                            item.setTitle(context.getString(R.string.category_pharmacies_and_perfumeries));
                            break;
                        case "SFZWW0J69CSTFT9G7OF8" :
                            item.setTitle(context.getString(R.string.category_supermarkets));
                            break;
                        case "4MZ3CESDY91JPIL3V0A0" :
                            item.setTitle(context.getString(R.string.category_tobacco));
                            break;
                        case "CWEXP75Q9UUME0BY3TL6" :
                            item.setTitle(context.getString(R.string.category_travel_and_transport));
                            break;
                        case "D8X5AARZ0QEW32TT7U76" :
                            item.setTitle(context.getString(R.string.category_utilities_and_bills));
                            break;
                        case "8WV9EANRD2KLBLLWXM28" :
                            item.setTitle(context.getString(R.string.category_medical_health_services));
                            break;
                        case "HZG3QNKUY87TPL4Q6SCB" :
                            item.setTitle(context.getString(R.string.category_marketplace));
                            break;
                        case "V0SCJ3VRA2F1FCQ8HW69" :
                            item.setTitle(context.getString(R.string.category_restaurants_and_fast_food));
                            break;
                        case "ZT5DYVLZT2QQCPM5FATK" :
                            item.setTitle(context.getString(R.string.category_other));
                            break;
                    }
                }

                r.setResult(categories);
            }
            sendCompletition(r);
        }

    };

}