package it.bancomatpay.sdk.manager.task;

import com.google.zxing.BarcodeFormat;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSetLoyaltyCardRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSetLoyaltyCardResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class SetLoyaltyCardTask extends ExtendedTask<String> {

    private String brandUuid;
    private String barCodeNumber;
    private String barCodeType;
    private DtoBrand.LoyaltyCardTypeEnum cardType;
    private String hexColor;
    private String brandName;
    private String brandLogoImage;

    public SetLoyaltyCardTask(OnCompleteResultListener<String> mListener, String brandUuid, String barCodeNumber, String barCodeType, DtoBrand.LoyaltyCardTypeEnum cardType, String hexColor, String brandName, String brandLogoImage) {
        super(mListener);
        this.brandUuid = brandUuid;
        this.barCodeNumber = barCodeNumber;
        this.barCodeType = barCodeType;
        this.cardType = cardType;
        this.hexColor = hexColor;
        this.brandName = brandName;
        this.brandLogoImage = brandLogoImage;
    }

    @Override
    protected void start() {
        DtoBrand brand = new DtoBrand();
        brand.setBrandUuid(brandUuid);
        brand.setBrandLogoImage(brandLogoImage);
        brand.setBrandName(brandName);
        brand.setHexColor(hexColor);
        brand.setType(cardType);

        DtoSetLoyaltyCardRequest req = new DtoSetLoyaltyCardRequest();
        req.setBarCodeNumber(barCodeNumber);
        req.setDtoBrand(brand);
        if (BancomatSdk.getInstance().checkBarcodeFormatValidity(barCodeNumber, barCodeType)) {
            req.setBarCodeType(barCodeType);
        } else {
            req.setBarCodeType(BarcodeFormat.CODE_128.toString());
        }

        Single.fromCallable(new HandleRequestInteractor<>(DtoSetLoyaltyCardResponse.class, req, Cmd.SET_LOYALTY_CARD, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoSetLoyaltyCardResponse>> listener = new NetworkListener<DtoSetLoyaltyCardResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoSetLoyaltyCardResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<String> result = new Result<>();
            prepareResult(result, response);
            if (result.isSuccess()){
                String loyaltyCardId = (Mapper.getLoyaltyCardId(response.getRes()));
                result.setResult(loyaltyCardId);
            }
            sendCompletition(result);
        }

    };

}
