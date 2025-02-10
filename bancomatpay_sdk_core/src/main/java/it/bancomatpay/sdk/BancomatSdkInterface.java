package it.bancomatpay.sdk;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.model.BCMOperation;
import it.bancomatpay.sdk.manager.model.BCMPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.network.dto.PaymentRequestType;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.OnProgressResultListener;
import it.bancomatpay.sdk.manager.task.model.AllowPaymentRequest;
import it.bancomatpay.sdk.manager.task.model.AtmConfirmWithdrawalData;
import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdk.manager.task.model.BankIdContactsData;
import it.bancomatpay.sdk.manager.task.model.BankIdMerchantData;
import it.bancomatpay.sdk.manager.task.model.BankIdRequestsData;
import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.CashbackData;
import it.bancomatpay.sdk.manager.task.model.CashbackStatusData;
import it.bancomatpay.sdk.manager.task.model.ConfirmPetrolPaymentData;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.DenyPaymentData;
import it.bancomatpay.sdk.manager.task.model.DirectDebitsHistoryData;
import it.bancomatpay.sdk.manager.task.model.DocumentImages;
import it.bancomatpay.sdk.manager.task.model.DocumentsData;
import it.bancomatpay.sdk.manager.task.model.EBankIdStatus;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardBrandsData;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardsData;
import it.bancomatpay.sdk.manager.task.model.LoyaltyJwtData;
import it.bancomatpay.sdk.manager.task.model.NotificationData;
import it.bancomatpay.sdk.manager.task.model.OutgoingPaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.PaymentData;
import it.bancomatpay.sdk.manager.task.model.PaymentHistoryData;
import it.bancomatpay.sdk.manager.task.model.PaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.PollingData;
import it.bancomatpay.sdk.manager.task.model.PosConfirmWithdrawalData;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.ShopList;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;
import it.bancomatpay.sdk.manager.task.model.TransactionDetails;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.task.model.UserThresholds;
import it.bancomatpay.sdk.manager.task.model.VerifyPaymentData;

public interface BancomatSdkInterface {

    void doInitSdk(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, Boolean needsCheckRoot, Boolean blockIfRooted, String abiCode, String groupCode);

    void doEnableUser(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String phonePrefix, String phoneNumber, String iban, String sessionToken);

    void getUserData(@NonNull Activity activity, @NonNull OnCompleteResultListener<UserData> listener, String sessionToken);

    void getSyncPhoneBook(@NonNull Activity activity, @NonNull OnCompleteResultListener<SyncPhoneBookData> listener, boolean forced, String sessionToken);

    void getShopList(@NonNull Activity activity, @NonNull OnCompleteResultListener<ArrayList<ShopItem>> listener, BcmLocation location, String sessionToken);

    void doGetShopByMerchantNameList(@NonNull Activity activity, @NonNull OnCompleteResultListener<ArrayList<ShopItem>> listener, String name, BcmLocation location, String sessionToken);

    void doGetMerchantDetail(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopItem> listener, String msisdn, String sessionToken);

    void doGetMerchantDetail(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopItem> listener, String tag, String shopId, String sessionToken);

    void doVerifyPaymentP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<VerifyPaymentData> listener, @NonNull BCMOperation payment, String sessionToken);

    void doVerifyPaymentP2B(@NonNull Activity activity, @NonNull OnCompleteResultListener<VerifyPaymentData> listener, @NonNull BCMOperation payment, String sessionToken);

    void doConfirmPaymentP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentData> listener, @NonNull BCMOperation payment, String sessionToken, String authorizationToken);

    void doConfirmPaymentP2B(@NonNull Activity activity, @NonNull OnProgressResultListener<PaymentData> listener, @NonNull BCMOperation payment, String sessionToken, String authorizationToken);

    void doCancelPreauthorization(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String paymentId, String paymentRequestId, String amount, String msisdnSender, String sessionToken);

    void doGetQrCodeDetails(@NonNull Activity activity, @NonNull OnCompleteResultListener<QrCodeDetailsData> listener, String qrCodeId, String sessionToken);

    void doGetPaymentHistory(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentHistoryData> listener, String msisdn, String sessionToken);

    void doAllowPaymentRequestP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<AllowPaymentRequest> listener, boolean allow, String msisdn, String sessionToken);

    void doAllowPaymentRequestP2B(@NonNull Activity activity, @NonNull OnCompleteResultListener<AllowPaymentRequest> listener, boolean allow, String msisdn, String sessionToken);

    void doGetBlacklistContacts(@NonNull Activity activity, @NonNull OnCompleteResultListener<ArrayList<ContactItem>> listener, String sessionToken);

    void doCancelPayment(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String paymentId, String description, String sessionToken);

    void doSetIncomingDefaultInstrument(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String iban, String sessionToken);

    void doSetOutgoingDefaultInstrument(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String iban, String sessionToken);

    void doConfirmPaymentRequestP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentData> listener, @NonNull BCMPaymentRequest paymentRequest, String sessionToken, String authorizationToken);

    void doConfirmPaymentRequestP2B(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentData> listener, @NonNull BCMPaymentRequest paymentRequest, String sessionToken, String authorizationToken);

    void doDenyPaymentRequestP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<DenyPaymentData> listener, @NonNull BCMPaymentRequest paymentRequest, boolean addToBlackList, String sessionToken);

    void doDenyPaymentRequestP2B(@NonNull Activity activity, @NonNull OnCompleteResultListener<DenyPaymentData> listener, @NonNull BCMPaymentRequest paymentRequest, String denyReason, String sessionToken);

    void doVerifyPaymentState(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentData> listener, String paymentId, PollingData pollingData, String sessionToken);

    void doSendPaymentRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentRequestData> listener, List<String> msisdnBeneficiary, String amount, String causal, ItemInterface itemInterface, String sessionToken);

    void doGetOutgoingPaymentRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<OutgoingPaymentRequestData> listener, String sessionToken);

    void doGetTransactionDetails(@NonNull Activity activity, @NonNull OnCompleteResultListener<TransactionDetails> listener, String paymentId, boolean isP2p, String sessionToken);

    void doDisableUser(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String sessionToken);

    void doGetThresholds(@NonNull Activity activity, @NonNull OnCompleteResultListener<UserThresholds> listener, String sessionToken);

    void doGetPaymentRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<NotificationData> listener, String paymentRequestId, PaymentRequestType paymentRequestType, String sessionToken);

    void doGetPaymentRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<NotificationData> listener, String sessionToken);

    void doGetLoyaltyCards(@NonNull Activity activity, @NonNull OnCompleteResultListener<LoyaltyCardsData> listener, String sessionToken);

    void doSetLoyaltyCard(@NonNull Activity activity, @NonNull OnCompleteResultListener<String> listener, String brandUuid, String barCodeNumber, String barCodeType, DtoBrand.LoyaltyCardTypeEnum cardType, String hexColor, String brandName, String brandLogoImage, String sessionToken);

    void doDeleteLoyaltyCard(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String barCodeNumber, String loyaltyCardId, String sessionToken);

    void doModifyLoyaltyCard(@NonNull Activity activity, @NonNull OnCompleteResultListener<LoyaltyCard> listener, String barCodeNumber, String barCodeType, String loyaltyCardId, String brandUuid, DtoBrand.LoyaltyCardTypeEnum cardType, String hexColor, String brandName, String brandLogoImage, String sessionToken);

    void doGetLoyaltyCardBrands(@NonNull Activity activity, @NonNull OnCompleteResultListener<LoyaltyCardBrandsData> listener, String sessionToken);

    void doGetDocuments(@NonNull Activity activity, @NonNull OnCompleteResultListener<DocumentsData> listener, String sessionToken);

    void doGetDocumentImages(@NonNull Activity activity, @NonNull OnCompleteResultListener<DocumentImages> listener, String documentUuid, String sessionToken);

    void doSetDocument(@NonNull Activity activity, @NonNull OnCompleteResultListener<String> listener, DtoDocument.DocumentTypeEnum documentType, String documentNumber, String surname, String name, String fiscalCode, String issuingInstituion, String issuingDate, String expirationDate, String note, String documentName, String sessionToken);

    void doSetDocumentImages(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String documentUuid, String frontImage, String backImage, String sessionToken);

    void doModifyDocument(@NonNull Activity activity, @NonNull OnCompleteResultListener<BcmDocument> listener,  String documentUuid, DtoDocument.DocumentTypeEnum documentType, String documentNumber, String surname, String name, String fiscalCode, String issuingInstitution, String issuingDate, String expirationDate, String note, String documentName, String sessionToken);

    void doDeleteDocument(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String documentUuid, String sessionToken);

	void doGetBankIdBlacklist(@NonNull Activity activity, @NonNull OnCompleteResultListener<ArrayList<BankIdMerchantData>> listener, String sessionToken);

	void doDeleteBankIdBlacklist(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String merchantTag, String merchantName, String sessionToken);

	void doGetBankIdRequests(@NonNull Activity activity, @NonNull OnCompleteResultListener<BankIdRequestsData> listener, String sessionToken);

	void doGetBankIdContacts(@NonNull Activity activity, @NonNull OnCompleteResultListener<BankIdContactsData> listener, String sessionToken);

	void doSetBankIdContacts(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String email, List<BankIdAddress> bankIdAddresses, String sessionToken);

	void doConfirmBankIdRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String requestId, String tag, String authorizationToken, String sessionToken);

	void doConfirmDirectDebitsRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String requestId, String tag, String authorizationToken, String sessionToken);

	void doGetBankIdStatus(@NonNull Activity activity, @NonNull OnCompleteResultListener<EBankIdStatus> listener, String sessionToken);

	void doSetBankIdStatus(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, EBankIdStatus status, String sessionToken);

	void doDenyBankIdRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String requestId, boolean blockMerchant, String sessionToken);

	void doDenyDirectDebitsRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String requestId, String merchantTag, String sessionToken);

	void doConfirmAtmWithdrawal(@NonNull Activity activity, @NonNull OnCompleteResultListener<AtmConfirmWithdrawalData> listener, String msisdn, String tag, long shopId, String amount, String authorizationToken, String qrCodeId, String sessionToken);

	void doConfirmPosWithdrawal(@NonNull Activity activity, @NonNull OnCompleteResultListener<PosConfirmWithdrawalData> listener, String tag, long shopId, BigInteger tillId, String amount, String totalAmount, String authorizationToken, String qrCodeId, String causal, String sessionToken);

	void doConfirmPetrolPayment(@NonNull Activity activity, @NonNull OnCompleteResultListener<ConfirmPetrolPaymentData> listener, String tag, String shopId, BigInteger tillId, String amount, String authorizationToken, String sessionToken);

    void doGetLoyaltyJwt(@NonNull Activity activity, @NonNull OnCompleteResultListener<LoyaltyJwtData> listener, String loyaltyToken, String sessionToken);

    void doGetDirectDebitsHistory(@NonNull Activity activity, @NonNull OnCompleteResultListener<DirectDebitsHistoryData> listener, String sessionToken);

    void doGetCashbackStatus(@NonNull Activity activity, @NonNull OnCompleteResultListener<CashbackStatusData> listener, String sessionToken);

    void doSubscribeCashback(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String authorizationToken, String sessionToken);

    void doUnsubscribeCashback(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String authorizationToken, String sessionToken);

    void doGetCashbackData(@NonNull Activity activity, @NonNull OnCompleteResultListener<CashbackData> listener, String sessionToken);

    void doDisableCashback(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String authorizationToken, String sessionToken);

    void doAcceptCashbackBpayTermsAndConditions(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String timestamp, String sessionToken);

    void doSplitBill(@NonNull Activity activity,  @NonNull OnCompleteResultListener<PaymentRequestData> listener, @NonNull ArrayList<? extends SplitBeneficiary> contactItems, @NonNull String totalAmount, @NonNull String causal, String description, String sessionToken);

    void doGetSplitBillHistory(@NonNull Activity activity, @NonNull OnCompleteResultListener<List<SplitBillHistory>> listener, String sessionToken);

    void doGetSplitBillHistoryDetail(@NonNull Activity activity,  @NonNull OnCompleteResultListener<List<SplitBeneficiary>> listener, String splitBillUUID, String sessionToken);

    void removeTasksListener(@NonNull Activity activity);

    class Factory {
        public static BancomatSdkInterface getInstance() {
            return BancomatSdk.getInstance();
        }
    }

}
