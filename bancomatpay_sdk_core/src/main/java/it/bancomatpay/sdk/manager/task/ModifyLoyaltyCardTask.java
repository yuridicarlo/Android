package it.bancomatpay.sdk.manager.task;

import com.google.zxing.BarcodeFormat;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.network.dto.DtoLoyaltyCard;
import it.bancomatpay.sdk.manager.network.dto.request.DtoModifyLoyaltyCardRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoModifyLoyaltyCardResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class ModifyLoyaltyCardTask extends ExtendedTask<LoyaltyCard> {

    private String loyaltyCardId;
    private DtoBrand.LoyaltyCardTypeEnum cardType;
    private String hexColor;
    private String brandName;
    private String brandLogoImage;
    private String barCodeNumber;
    private String barCodeType;
    private String brandUuid;

    public ModifyLoyaltyCardTask(OnCompleteResultListener<LoyaltyCard> mListener, String barcodeNumber, String barCodeType, String loyaltyCardId, String brandUuid, DtoBrand.LoyaltyCardTypeEnum cardType, String hexColor, String brandName, String brandLogoImage) {
        super(mListener);
        this.barCodeNumber = barcodeNumber;
        this.barCodeType = barCodeType;
        this.loyaltyCardId = loyaltyCardId;
        this.cardType = cardType;
        this.hexColor = hexColor;
        this.brandName = brandName;
        this.brandLogoImage = brandLogoImage;
        this.brandUuid = brandUuid;
    }

    @Override
    protected void start() {
        DtoBrand brand = new DtoBrand();
        brand.setBrandLogoImage(brandLogoImage);
        brand.setBrandName(brandName);
        brand.setHexColor(hexColor);
        brand.setType(cardType);
        brand.setBrandUuid(brandUuid);

        DtoModifyLoyaltyCardRequest req = new DtoModifyLoyaltyCardRequest();
        DtoLoyaltyCard loyaltyCard = new DtoLoyaltyCard();
        loyaltyCard.setDtoBrand(brand);
        loyaltyCard.setLoyaltyCardId(loyaltyCardId);
        loyaltyCard.setBarCodeNumber(barCodeNumber);
        if (BancomatSdk.getInstance().checkBarcodeFormatValidity(barCodeNumber, barCodeType)) {
            loyaltyCard.setBarCodeType(barCodeType);
        } else {
            loyaltyCard.setBarCodeType(BarcodeFormat.CODE_128.toString());
        }

        req.setDtoLoyaltyCard(loyaltyCard);

        Single.fromCallable(new HandleRequestInteractor<>(DtoModifyLoyaltyCardResponse.class, req, Cmd.MODIFY_LOYALTY_CARD, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoModifyLoyaltyCardResponse>> listener = new NetworkListener<DtoModifyLoyaltyCardResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoModifyLoyaltyCardResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<LoyaltyCard> result = new Result<>();
            prepareResult(result, response);
            if (result.isSuccess()) {
                result.setResult(Mapper.getLoyaltyCard(response.getRes().getDtoLoyaltyCard()));
            }
            sendCompletition(result);
        }

    };

}
