package it.bancomatpay.sdk;

import androidx.annotation.NonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.network.dto.PaymentRequestType;
import it.bancomatpay.sdk.manager.task.AllowPaymentRequestTask;
import it.bancomatpay.sdk.manager.task.CancelPaymentTask;
import it.bancomatpay.sdk.manager.task.CancelPreauthorizationTask;
import it.bancomatpay.sdk.manager.task.ConfirmPaymentRequestUnencryptedTask;
import it.bancomatpay.sdk.manager.task.ConfirmPaymentUnencryptedTask;
import it.bancomatpay.sdk.manager.task.DelayTask;
import it.bancomatpay.sdk.manager.task.DeleteDocumentTask;
import it.bancomatpay.sdk.manager.task.DeleteLoyaltyCardTask;
import it.bancomatpay.sdk.manager.task.DenyPaymentRequestTask;
import it.bancomatpay.sdk.manager.task.DisableUserTask;
import it.bancomatpay.sdk.manager.task.EnableUserTask;
import it.bancomatpay.sdk.manager.task.GetBlacklistContactsTask;
import it.bancomatpay.sdk.manager.task.GetDocumentImagesTask;
import it.bancomatpay.sdk.manager.task.GetDocumentsTask;
import it.bancomatpay.sdk.manager.task.GetLoyaltyCardBrandsTask;
import it.bancomatpay.sdk.manager.task.GetLoyaltyCardsTask;
import it.bancomatpay.sdk.manager.task.GetMerchantDetailsTask;
import it.bancomatpay.sdk.manager.task.GetOutgoingPaymentRequestTask;
import it.bancomatpay.sdk.manager.task.GetPaymentHistoryTask;
import it.bancomatpay.sdk.manager.task.GetPaymentRequestTask;
import it.bancomatpay.sdk.manager.task.GetQrCodeDetailsTask;
import it.bancomatpay.sdk.manager.task.GetShopListByMerchantNameTask;
import it.bancomatpay.sdk.manager.task.GetThresholdsTask;
import it.bancomatpay.sdk.manager.task.GetTransactionDetailsTask;
import it.bancomatpay.sdk.manager.task.InitSdkTask;
import it.bancomatpay.sdk.manager.task.ModifyDocumentTask;
import it.bancomatpay.sdk.manager.task.ModifyLoyaltyCardTask;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.OnProgressResultListener;
import it.bancomatpay.sdk.manager.task.SendPaymentRequestTask;
import it.bancomatpay.sdk.manager.task.SetDocumentImagesTask;
import it.bancomatpay.sdk.manager.task.SetDocumentTask;
import it.bancomatpay.sdk.manager.task.SetIncomingDefaultInstrumentTask;
import it.bancomatpay.sdk.manager.task.SetLoyaltyCardTask;
import it.bancomatpay.sdk.manager.task.SetOutgoingDefaultInstrumentTask;
import it.bancomatpay.sdk.manager.task.SingleTask;
import it.bancomatpay.sdk.manager.task.SplitBillHistoryDetailTask;
import it.bancomatpay.sdk.manager.task.SplitBillHistoryTask;
import it.bancomatpay.sdk.manager.task.SplitBillTask;
import it.bancomatpay.sdk.manager.task.VerifyPaymentStateTask;
import it.bancomatpay.sdk.manager.task.VerifyPaymentTask;
import it.bancomatpay.sdk.manager.task.model.AllowPaymentRequest;
import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.DenyPaymentData;
import it.bancomatpay.sdk.manager.task.model.DocumentImages;
import it.bancomatpay.sdk.manager.task.model.DocumentsData;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardBrandsData;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardsData;
import it.bancomatpay.sdk.manager.task.model.NotificationData;
import it.bancomatpay.sdk.manager.task.model.OutgoingPaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.PaymentData;
import it.bancomatpay.sdk.manager.task.model.PaymentHistoryData;
import it.bancomatpay.sdk.manager.task.model.PaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.PollingData;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.ShopList;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.task.model.TransactionDetails;
import it.bancomatpay.sdk.manager.task.model.UserThresholds;
import it.bancomatpay.sdk.manager.task.model.VerifyPaymentData;

class TaskManager {

    private static TaskManager instance;

    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    Task<?> doInitSdk(OnCompleteResultListener<Void> listener, Boolean checkRoot, Boolean blockIfRooted, String abiCode, String groupCode) {
        return new InitSdkTask(listener, checkRoot, blockIfRooted, abiCode, groupCode);
    }

    Task<?> doEnableUser(OnCompleteResultListener<Void> listener, String phonePrefix, String phoneNumber, String iban) {
        return new EnableUserTask(listener, phonePrefix, phoneNumber, iban);
    }

    Task<?> doGetShopByMerchantNameList(OnCompleteResultListener<ArrayList<ShopItem>> listener, String name, BcmLocation location) {
        GetShopListByMerchantNameTask merchantNameTask = new GetShopListByMerchantNameTask(listener, name, location);
        DelayTask<ArrayList<ShopItem>> delayTask = new DelayTask<>(listener, merchantNameTask, 1500);
        return new SingleTask<>(listener, delayTask);
    }

    Task<?> doGetMerchantDetail(OnCompleteResultListener<ShopItem> listener, String msisdn) {
        return new GetMerchantDetailsTask(listener, msisdn);
    }

    Task<?> doGetMerchantDetail(OnCompleteResultListener<ShopItem> listener, String tag, String shopId) {
        return new GetMerchantDetailsTask(listener, tag, shopId);
    }

    Task<?> doVerifyPaymentP2P(OnCompleteResultListener<VerifyPaymentData> listener, String msisdn, int amount, String paymentRequestId, String causal) {
        return new VerifyPaymentTask(listener, msisdn, amount, paymentRequestId, causal);
    }

    Task<?> doVerifyPaymentP2B(OnCompleteResultListener<VerifyPaymentData> listener, String tag, Long shopId, BigInteger tillId, int amount) {
        return new VerifyPaymentTask(listener, tag, shopId, tillId, amount);
    }

    Task<?> doVerifyPaymentP2B(OnCompleteResultListener<VerifyPaymentData> listener, String tag, Long shopId, BigInteger tillId, int amount, String qrCodeId, String causal) {
        return new VerifyPaymentTask(listener, tag, shopId, tillId, amount, qrCodeId, causal);
    }

    Task<?> doConfirmPaymentP2P(OnCompleteResultListener<PaymentData> mListener, ItemInterface itemInterface, String paymentId, String paymentRequestId, String msisdn, int amount, String causal, String authorizationToken) {
        return new ConfirmPaymentUnencryptedTask(mListener, itemInterface, paymentId, paymentRequestId, msisdn, amount, causal, authorizationToken);
    }

    Task<?> doConfirmPaymentP2B(OnProgressResultListener<PaymentData> mListener, ItemInterface itemInterface, String paymentId, String tag, Long shopId, BigInteger tillId, int amount, String causal, String qrCodeId, boolean qrCodeEmpsa, String authorizationToken) {
        return new ConfirmPaymentUnencryptedTask(mListener, itemInterface, paymentId, tag, shopId, tillId, amount, causal, qrCodeId, qrCodeEmpsa, authorizationToken);
    }

    Task<?> doCancelPreauthorization(OnCompleteResultListener<Void> mListener, String paymentId, String paymentRequestId, String amount, String msisdnSender) {
        return new CancelPreauthorizationTask(mListener, paymentId, paymentRequestId, amount, msisdnSender);
    }

    Task<?> doGetQrCodeDetails(OnCompleteResultListener<QrCodeDetailsData> mListener, String qrCodeId) {
        return new GetQrCodeDetailsTask(mListener, qrCodeId);
    }

    Task<?> doGetPaymentHistory(OnCompleteResultListener<PaymentHistoryData> mListener, String msisdn) {
        return new GetPaymentHistoryTask(mListener, msisdn);
    }

    Task<?> doAllowPaymentRequestP2P(OnCompleteResultListener<AllowPaymentRequest> mListener, boolean allow, String msisdn) {
        return new AllowPaymentRequestTask(mListener, allow, null, msisdn);
    }

    Task<?> doAllowPaymentRequestP2B(OnCompleteResultListener<AllowPaymentRequest> mListener, boolean allow, String msisdn) {
        return new AllowPaymentRequestTask(mListener, null, allow, msisdn);
    }

    Task<?> doGetBlacklistContacts(OnCompleteResultListener<ArrayList<ContactItem>> mListener) {
        return new GetBlacklistContactsTask(mListener);
    }

    Task<?> doCancelPayment(OnCompleteResultListener<Void> mListener, String paymentId, String description) {
        return new CancelPaymentTask(mListener, paymentId, description);
    }

    Task<?> doSetIncomingDefaultInstrument(OnCompleteResultListener<Void> mListener, String iban) {
        return new SetIncomingDefaultInstrumentTask(mListener, iban);
    }

    Task<?> doSetOutgoingDefaultInstrument(OnCompleteResultListener<Void> mListener, String iban) {
        return new SetOutgoingDefaultInstrumentTask(mListener, iban);
    }

    Task<?> doConfirmPaymentRequestP2P(OnCompleteResultListener<PaymentData> mListener, ItemInterface itemInterface, String paymentId, String paymentRequestId, String msisdn, int amount, String causal, String authorizationToken) {
        return new ConfirmPaymentRequestUnencryptedTask(mListener, itemInterface, paymentId, paymentRequestId, msisdn, amount, causal, authorizationToken);
    }

    Task<?> doConfirmPaymentRequestP2B(OnCompleteResultListener<PaymentData> mListener, ItemInterface itemInterface, String paymentId, String tag, Long shopId, BigInteger tillId, int amount, String causal, String authorizationToken) {
        return new ConfirmPaymentRequestUnencryptedTask(mListener, itemInterface, paymentId, tag, shopId, tillId, amount, causal, authorizationToken);
    }

    Task<?> doDenyPaymentRequestP2P(OnCompleteResultListener<DenyPaymentData> mListener, String paymentId, String msisdn, boolean addToBlackList) {
        return new DenyPaymentRequestTask(mListener, paymentId, msisdn, addToBlackList);
    }

    Task<?> doDenyPaymentRequestP2B(OnCompleteResultListener<DenyPaymentData> mListener, String paymentId, String denyReason) {
        return new DenyPaymentRequestTask(mListener, paymentId, denyReason);
    }

    Task<?> doVerifyPaymentState(OnCompleteResultListener<PaymentData> mListener, String paymentId, PollingData pollingData ) {
        return new VerifyPaymentStateTask(mListener, paymentId, pollingData);
    }

    Task<?> doSendPaymentRequest(OnCompleteResultListener<PaymentRequestData> mListener, List<String> msisdnBeneficiary, String amount, String causal, ItemInterface itemInterface) {
        return new SendPaymentRequestTask(mListener, msisdnBeneficiary, amount, causal, itemInterface);
    }

    Task<?> doGetOutgoingPaymentRequest(OnCompleteResultListener<OutgoingPaymentRequestData> mListener) {
        return new GetOutgoingPaymentRequestTask(mListener);
    }

    Task<?> doGetTransactionDetails(OnCompleteResultListener<TransactionDetails> mListener, String paymentId, boolean isP2p) {
        return new GetTransactionDetailsTask(mListener, paymentId, isP2p);
    }

    Task<?> doDisableUser(OnCompleteResultListener<Void> mListener) {
        return new DisableUserTask(mListener);
    }

    Task<?> doGetPaymentRequest(OnCompleteResultListener<NotificationData> mListener) {
        return new GetPaymentRequestTask(mListener);
    }

    Task<?> doGetThresholds(OnCompleteResultListener<UserThresholds> mListener) {
        return new GetThresholdsTask(mListener);
    }

    Task<?> doGetPaymentRequest(OnCompleteResultListener<NotificationData> mListener, String paymentRequestId, PaymentRequestType paymentRequestType) {
        return new GetPaymentRequestTask(mListener, paymentRequestId, paymentRequestType);
    }

    Task<?> doGetLoyaltyCards(OnCompleteResultListener<LoyaltyCardsData> mListener) {
        return new GetLoyaltyCardsTask(mListener);
    }

    Task<?> doSetLoyaltyCard(OnCompleteResultListener<String> mListener, String brandUuid, String barCodeNumber, String barCodeType, DtoBrand.LoyaltyCardTypeEnum cardType, String hexColor, String brandName, String brandLogoImage) {
        return new SetLoyaltyCardTask(mListener, brandUuid, barCodeNumber, barCodeType, cardType, hexColor, brandName, brandLogoImage);
    }

    Task<?> doDeleteLoyaltyCard(OnCompleteResultListener<Void> mListener, String barCodeNumber, String loyaltyCardId) {
        return new DeleteLoyaltyCardTask(mListener, barCodeNumber, loyaltyCardId);
    }

    Task<?> doModifyLoyaltyCard(OnCompleteResultListener<LoyaltyCard> mListener, String barCodeNumber, String barCodeType, String loyaltyCardId, String brandUuid, DtoBrand.LoyaltyCardTypeEnum cardType, String hexColor, String brandName, String brandLogoImage) {
        return new ModifyLoyaltyCardTask(mListener, barCodeNumber, barCodeType, loyaltyCardId, brandUuid, cardType, hexColor, brandName, brandLogoImage);
    }

    Task<?> doGetLoyaltyCardBrands(OnCompleteResultListener<LoyaltyCardBrandsData> mListener) {
        return new GetLoyaltyCardBrandsTask(mListener);
    }

    Task<?> doGetDocumentList(OnCompleteResultListener<DocumentsData> mListener) {
        return new GetDocumentsTask(mListener);
    }
    Task<?> doGetDocumentImages(OnCompleteResultListener<DocumentImages> mListener, String documentUuid) {
        return new GetDocumentImagesTask(mListener, documentUuid);
    }
    Task<?> doSetDocument(OnCompleteResultListener<String> mListener, DtoDocument.DocumentTypeEnum documentType, String documentNumber, String surname, String name, String fiscalCode, String issuingInstituion, String issuingDate, String expirationDate, String note, String documentName){
        return new SetDocumentTask(mListener, documentType, documentNumber, surname, name, fiscalCode, issuingInstituion, issuingDate, expirationDate, note, documentName);
    }

    Task<?> doSetDocumentImages(OnCompleteResultListener<Void> mListener, String documentUuid, String frontImage, String backImage) {
        return new SetDocumentImagesTask(mListener, documentUuid, frontImage, backImage);
    }

    Task<?> doDeleteDocument(OnCompleteResultListener<Void> mListener, String documentUuid) {
        return new DeleteDocumentTask(mListener, documentUuid);
    }

    Task<?> doModifyDocument(OnCompleteResultListener<BcmDocument> listener, String documentUuid, DtoDocument.DocumentTypeEnum documentType, String documentNumber, String surname, String name, String fiscalCode, String issuingInstitution, String issuingDate, String expirationDate, String note, String documentName) {
        return new ModifyDocumentTask(listener, documentUuid, documentType, documentNumber, surname, name, fiscalCode, issuingInstitution, issuingDate, expirationDate, note, documentName);
    }

    Task<?> doSplitBill(@NonNull OnCompleteResultListener<PaymentRequestData> listener, @NonNull ArrayList<? extends SplitBeneficiary> contactItems, @NonNull String totalAmount, @NonNull String causal, String description) {
        return new SplitBillTask(listener, contactItems, totalAmount, causal, description);
    }

    Task<?> doGetSplitBillHistory(@NonNull OnCompleteResultListener<List<SplitBillHistory>> listener) {
        return new SplitBillHistoryTask(listener);
    }

    Task<?> doGetSplitBillHistoryDetail(@NonNull OnCompleteResultListener<List<SplitBeneficiary>> listener, String splitBillUUID) {
        return new SplitBillHistoryDetailTask(listener, splitBillUUID);
    }

}
