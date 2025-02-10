package it.bancomatpay.sdk.manager.utilities;

import android.graphics.Color;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.bancomatpay.sdk.R;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.network.dto.DtoAddress;
import it.bancomatpay.sdk.manager.network.dto.DtoBankIdAddress;
import it.bancomatpay.sdk.manager.network.dto.DtoBankIdMerchant;
import it.bancomatpay.sdk.manager.network.dto.DtoBankIdRequest;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.network.dto.DtoCustomerJourneyTag;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.network.dto.DtoInstrument;
import it.bancomatpay.sdk.manager.network.dto.DtoLoyaltyCard;
import it.bancomatpay.sdk.manager.network.dto.DtoPayment;
import it.bancomatpay.sdk.manager.network.dto.DtoShop;
import it.bancomatpay.sdk.manager.network.dto.DtoSplitBill;
import it.bancomatpay.sdk.manager.network.dto.DtoStatus;
import it.bancomatpay.sdk.manager.network.dto.DtoThreshold;
import it.bancomatpay.sdk.manager.network.dto.DtoTill;
import it.bancomatpay.sdk.manager.network.dto.DtoTransaction;
import it.bancomatpay.sdk.manager.network.dto.PaymentStateType;
import it.bancomatpay.sdk.manager.network.dto.TransactionStatusEnum;
import it.bancomatpay.sdk.manager.network.dto.request.DtoP2BPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.request.DtoP2PPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.request.DtoPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoConfirmAtmWithdrawalResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoConfirmPetrolPaymentResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoConfirmPosWithdrawalResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoDirectDebitHistoryElement;
import it.bancomatpay.sdk.manager.network.dto.response.DtoDirectDebitRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetBankIdBlacklistResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetBankIdContactsResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetBankIdRequestsResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetBlackListContactsResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetCashbackDataResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetCashbackStatusResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetDocumentImagesResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetLoyaltyCardBrandsResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetLoyaltyCardsResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetLoyaltyJwtResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetOutgoingPaymentRequestResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetPaymentRequestResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetThresholdsResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetTransactionDetailsResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetUserDataResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoModifyDocumentResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSendPaymentRequestResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSetLoyaltyCardResponse;
import it.bancomatpay.sdk.manager.network.dto.DtoSplitBillDetail;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSplitBillHistoryDetailResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSplitBillHistoryResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoVerifyPaymentResponse;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdk.manager.task.model.Address;
import it.bancomatpay.sdk.manager.task.model.AtmConfirmWithdrawalData;
import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdk.manager.task.model.BankIdContactsData;
import it.bancomatpay.sdk.manager.task.model.BankIdMerchantData;
import it.bancomatpay.sdk.manager.task.model.BankIdRequest;
import it.bancomatpay.sdk.manager.task.model.BankIdRequestsData;
import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdk.manager.task.model.CashbackData;
import it.bancomatpay.sdk.manager.task.model.CashbackStatusData;
import it.bancomatpay.sdk.manager.task.model.ConfirmPetrolPaymentData;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyConsents;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;
import it.bancomatpay.sdk.manager.task.model.DirectDebitHistoryElement;
import it.bancomatpay.sdk.manager.task.model.DirectDebitRequest;
import it.bancomatpay.sdk.manager.task.model.DirectDebitsHistoryData;
import it.bancomatpay.sdk.manager.task.model.DocumentImages;
import it.bancomatpay.sdk.manager.task.model.DocumentsData;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;
import it.bancomatpay.sdk.manager.task.model.LoyaltyBrand;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardBrandsData;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardsData;
import it.bancomatpay.sdk.manager.task.model.LoyaltyJwtData;
import it.bancomatpay.sdk.manager.task.model.NotificationData;
import it.bancomatpay.sdk.manager.task.model.NotificationPaymentData;
import it.bancomatpay.sdk.manager.task.model.OutgoingPaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.PaymentData;
import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.PaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.PosConfirmWithdrawalData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;
import it.bancomatpay.sdk.manager.task.model.Thresholds;
import it.bancomatpay.sdk.manager.task.model.Till;
import it.bancomatpay.sdk.manager.task.model.TransactionData;
import it.bancomatpay.sdk.manager.task.model.TransactionDataOutgoing;
import it.bancomatpay.sdk.manager.task.model.TransactionDetails;
import it.bancomatpay.sdk.manager.task.model.TransactionType;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.task.model.UserThresholds;
import it.bancomatpay.sdk.manager.task.model.VerifyPaymentData;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeWrapper;

public class Mapper {

    private static final String TAG = Mapper.class.getSimpleName();

    private Mapper() {
    }

    public static PaymentData.State gePaymentState(PaymentStateType paymentStateType) {
        switch (paymentStateType) {
            case EXECUTED:
                return PaymentData.State.EXECUTED;
            case FAILED:
                return PaymentData.State.FAILED;
            case PENDING:
                return PaymentData.State.PENDING;
            case AUTHORIZED:
                return PaymentData.State.AUTHORIZED;
            case WAIT:
                return PaymentData.State.WAIT;
            default:
                return null;
        }
    }

    private static Thresholds getThresholdArrayList(List<DtoThreshold> dtoThresholds) {
        Thresholds thresholds = new Thresholds();
        if (dtoThresholds != null) {
            for (DtoThreshold dtoThreshold : dtoThresholds) {
                switch (dtoThreshold.getThresholdType()) {
                    case "SP":
                        thresholds.setThresholdTransactionMaxValue(Long.parseLong(dtoThreshold.getThresholdMaxValue()));
                        break;
                    case "M":
                        thresholds.setThresholdMonthMaxValue(Long.parseLong(dtoThreshold.getThresholdMaxValue()));
                        thresholds.setThresholdMonthValue(Long.parseLong(dtoThreshold.getThresholdValue()));
                        thresholds.setThresholdMonthMinValue(Long.parseLong(dtoThreshold.getThresholdMinValue()));
                        break;
                    case "G":
                        thresholds.setThresholdDayMaxValue(Long.parseLong(dtoThreshold.getThresholdMaxValue()));
                        thresholds.setThresholdDayValue(Long.parseLong(dtoThreshold.getThresholdValue()));
                        thresholds.setThresholdDayMinValue(Long.parseLong(dtoThreshold.getThresholdMinValue()));
                        break;
                }
            }
        }
        return thresholds;
    }


    public static VerifyPaymentData getVerifyPaymentData(DtoVerifyPaymentResponse dtoVerifyPaymentResponse) {
        VerifyPaymentData verifyPaymentData = new VerifyPaymentData();
        verifyPaymentData.setFee(BigDecimalUtils.getBigDecimalFromCents(dtoVerifyPaymentResponse.getFee()));
        verifyPaymentData.setPaymentId(dtoVerifyPaymentResponse.getPaymentId());
        verifyPaymentData.setContactName(dtoVerifyPaymentResponse.getContactName());
        return verifyPaymentData;
    }

    public static ArrayList<ShopItem> getShopItems(List<DtoShop> dtoShops) {
        ArrayList<ShopItem> shopItems = new ArrayList<>();
        for (DtoShop dtoShop : dtoShops) {
            shopItems.add(getShopItem(dtoShop));
        }
        return shopItems;
    }

    private static Address getAddress(DtoAddress dtoAddress) {
        Address address = null;
        if (dtoAddress != null) {
            address = new Address();
            address.setCity(dtoAddress.getCity());
            address.setPostalCode(dtoAddress.getPostalCode());
            address.setProvince(dtoAddress.getProvince());
            address.setStreet(dtoAddress.getStreet());
        }
        return address;
    }

    public static ShopItem getShopItem(DtoShop dtoShop) {
        ShopItem shopItem = new ShopItem();
        shopItem.setName(dtoShop.getMerchantName());
        shopItem.setAddress(getAddress(dtoShop.getAddress()));
        shopItem.setDistance(BigDecimalUtils.getBigDecimalFromThousandths(dtoShop.getDistance()));
        shopItem.setHolderName(dtoShop.getHolderName());
        shopItem.setInsignia(dtoShop.getInsignia());
        shopItem.setLatitude(dtoShop.getLatitude());
        shopItem.setLongitude(dtoShop.getLongitude());
        shopItem.setMail(dtoShop.getMail());
        shopItem.setMsisdn(dtoShop.getMsisdn());
        shopItem.setShopId(dtoShop.getShopId());
        shopItem.setTag(dtoShop.getTag());
        shopItem.setTillManagement(dtoShop.isTillManagement());
        shopItem.setTillList(getTillList(dtoShop.getTills()));
        shopItem.setMccImageName(dtoShop.getMccImageName());
        try {
            shopItem.setMerchantType(ShopItem.EMerchantType.valueOf(dtoShop.getMerchantType()));
        } catch (Exception e) {
            shopItem.setMerchantType(ShopItem.EMerchantType.STANDARD);
        }
        return shopItem;
    }

    private static List<Till> getTillList(List<DtoTill> dtoTillList) {
        List<Till> tills = new ArrayList<>();
        if (dtoTillList != null) {
            for (DtoTill item : dtoTillList) {
                Till till = new Till();
                till.setAutomatic(item.isAutomatic());
                till.setEnabled(item.isEnabled());
                till.setIdentifier(item.getIdentifier());
                till.setName(item.getIdentifier());
                till.setTillId(item.getTillId());

                tills.add(till);
            }
        }
        return tills;
    }

    public static ConfirmPetrolPaymentData getConfirmPetrolPaymentData(DtoConfirmPetrolPaymentResponse response) {
        ConfirmPetrolPaymentData data = new ConfirmPetrolPaymentData();
        try {
            data.setPetrolPaymentState(ConfirmPetrolPaymentData.EPetrolPaymentState.valueOf(response.getPetrolPaymentState()));
        } catch (IllegalArgumentException e) {
            data.setPetrolPaymentState(ConfirmPetrolPaymentData.EPetrolPaymentState.FAILED);
        }
        return data;
    }

    public static PaymentItem getPaymentItem(DtoPayment dtoPayment) {
        PaymentItem paymentItem = new PaymentItem();
        paymentItem.setAddress(getAddress(dtoPayment.getAddress()));
        paymentItem.setInsignia(dtoPayment.getInsignia());
        paymentItem.setMsisdn(dtoPayment.getMsisdn());
        paymentItem.setShopId(dtoPayment.getShopId());
        paymentItem.setTag(dtoPayment.getTag());
        paymentItem.setAmount(BigDecimalUtils.getBigDecimalFromCents(dtoPayment.getAmount()));
        if (!TextUtils.isEmpty(dtoPayment.getAmount())) {
            paymentItem.setCentsAmount(Integer.parseInt(dtoPayment.getAmount()));
        }
        if (!TextUtils.isEmpty(dtoPayment.getTotalAmount())) {
            paymentItem.setCentsTotalAmount(Integer.parseInt(dtoPayment.getTotalAmount()));
        }
        paymentItem.setCausal(dtoPayment.getCausal());
        paymentItem.setFee(BigDecimalUtils.getBigDecimalFromCents(dtoPayment.getFee()));
        paymentItem.setTillId(dtoPayment.getTillId());
        paymentItem.setPaymentId(dtoPayment.getPaymentId());
        paymentItem.setShopName(dtoPayment.getShopName());
        paymentItem.setPaymentId(dtoPayment.getPaymentId());
        if (dtoPayment.getCategory() != null) {
            paymentItem.setCategory(PaymentItem.EPaymentCategory.fromValue(dtoPayment.getCategory()));
        }
        paymentItem.setLatitude(dtoPayment.getLatitude());
        paymentItem.setLongitude(dtoPayment.getLongitude());

        paymentItem.setLocalCurrency(dtoPayment.getLocalCurrency());

        if (!TextUtils.isEmpty(dtoPayment.getLocalAmount())) {
            paymentItem.setLocalAmount(BigDecimalUtils.getBigDecimalFromCents(dtoPayment.getLocalAmount()));
        }

        paymentItem.setQrCodeEmpsa(dtoPayment.isQrCodeEmpsa());

        return paymentItem;
    }

    public static UserData getUserData(DtoGetUserDataResponse dtoGetHomePageDetailsResponse) {
        UserData userData = new UserData();
        userData.setMsisdn(dtoGetHomePageDetailsResponse.getMsisdn());
        if (!TextUtils.isEmpty(dtoGetHomePageDetailsResponse.getSurname())) {
            userData.setSurname(dtoGetHomePageDetailsResponse.getSurname());
        } else {
            userData.setSurname("");
        }
        if (!TextUtils.isEmpty(dtoGetHomePageDetailsResponse.getName())) {
            userData.setName(dtoGetHomePageDetailsResponse.getName());
        } else {
            userData.setName("");
        }
        if (dtoGetHomePageDetailsResponse.getBalance() != null) {
            userData.setBalance(BigDecimalUtils.getBigDecimalFromCents(dtoGetHomePageDetailsResponse.getBalance().intValue()));
        } else {
            userData.setBalance(null);
        }
        userData.setDefaultReceiver(dtoGetHomePageDetailsResponse.isIsDefaultReceiver());
        //homePageDetailsData.setCipheredIban(dtoGetHomePageDetailsResponse.getCipheredIban());
        userData.setIban(dtoGetHomePageDetailsResponse.getIban());
        userData.setP2BThresholds(getThresholdArrayList(dtoGetHomePageDetailsResponse.getP2BThresholds()));
        userData.setP2PThresholds(getThresholdArrayList(dtoGetHomePageDetailsResponse.getP2PThresholds()));
        userData.setPaymentRequestNumber(dtoGetHomePageDetailsResponse.getPaymentRequestNumber() != null ? BigInteger.valueOf(dtoGetHomePageDetailsResponse.getPaymentRequestNumber()) : new BigInteger("0"));
        userData.setDefaultReceiverOtherBank(dtoGetHomePageDetailsResponse.isDefaultReceiverOtherBank());
        userData.setMultiIban(dtoGetHomePageDetailsResponse.isMultiIban());
        userData.setInstruments(getInstruments(dtoGetHomePageDetailsResponse.getInstruments()));

        if (dtoGetHomePageDetailsResponse.getConsents() != null) {
            CustomerJourneyConsents consents = new CustomerJourneyConsents();
            consents.setProfiling(dtoGetHomePageDetailsResponse.getConsents().isProfiling());
            consents.setMarketing(dtoGetHomePageDetailsResponse.getConsents().isMarketing());
            consents.setDataToThirdParties(dtoGetHomePageDetailsResponse.getConsents().isDataToThirdParties());
            userData.setCustomerJourneyConsents(consents);
        } else {
            userData.setCustomerJourneyConsents(null);
        }

        return userData;
    }

    public static UserThresholds getThresholds(DtoGetThresholdsResponse dtoThresholds) {
        UserThresholds thresholds = new UserThresholds();
        thresholds.setP2BThresholds(getThresholdArrayList(dtoThresholds.getP2bThresholds()));
        thresholds.setP2PThresholds(getThresholdArrayList(dtoThresholds.getP2pThresholds()));
        return thresholds;
    }

    public static ArrayList<TransactionData> getTransactions(List<DtoTransaction> dtoTransactionList) {
        ArrayList<TransactionData> transactionDatas = new ArrayList<>();
        for (DtoTransaction dtoTransaction : dtoTransactionList) {
            transactionDatas.add(getTransaction(dtoTransaction));
        }
        return transactionDatas;
    }

    private static TransactionData getTransaction(DtoTransaction dtoTransaction) {
        TransactionData transactionData = new TransactionData();
        transactionData.setIban(dtoTransaction.getIban());
        transactionData.setMsisdn(dtoTransaction.getMsisdn());
        transactionData.setAmount(BigDecimalUtils.getBigDecimalFromCents(dtoTransaction.getAmount()));
        transactionData.setTotalAmount(BigDecimalUtils.getBigDecimalFromCents(dtoTransaction.getTotalAmount()));
        transactionData.setCashbackAmount(BigDecimalUtils.getBigDecimalFromCents(dtoTransaction.getCashbackAmount()));
        transactionData.setCausal(dtoTransaction.getCausal());
        transactionData.setPaymentDate(getDate(dtoTransaction.getPaymentDate()));
        transactionData.setRequestDate(getDate(dtoTransaction.getRequestDate()));
        transactionData.setTransactionType(getTransactionType(dtoTransaction.getTransactionType()));
        transactionData.setTransactionStatus(getStatus(dtoTransaction.getTransactionStatus()));
        transactionData.setLetter(getLetter(dtoTransaction.getDisplayName()));
        transactionData.setTag(dtoTransaction.getTag());
        transactionData.setTransactionId(dtoTransaction.getTransactionId());
        transactionData.setIdSct(dtoTransaction.getIdSct());
        if (transactionData.getTransactionType() == TransactionType.P2B) {
            transactionData.setItemInterface(getShopItem(dtoTransaction.getDtoShop()));
            transactionData.setDisplayName(dtoTransaction.getDisplayName());
        } else {
            ContactItem contactItem = new ContactItem();
            contactItem.setMsisdn(dtoTransaction.getMsisdn());
            transactionData.setItemInterface(contactItem);
        }
        if (!TextUtils.isEmpty(dtoTransaction.getFee())) {
            transactionData.setFee(dtoTransaction.getFee());
        }
        return transactionData;
    }

    private static String getLetter(String displayName) {
        if (TextUtils.isEmpty(displayName)) {
            return "#";
        }
        return displayName.substring(0, 1).toUpperCase();
    }

    private static TransactionType getTransactionType(String transactionTypeEnum) {
        switch (transactionTypeEnum) {
            case "P2B":
                return TransactionType.P2B;
            case "B2P":
                return TransactionType.B2P;
            case "P2P":
                return TransactionType.P2P;
            default:
                return null;
        }
    }

    private static TransactionData.Status getStatus(TransactionStatusEnum transactionStatusEnum) {
        switch (transactionStatusEnum) {
            case ANN_P2P:
                return TransactionData.Status.ANN_P2P;
            case ANN_P2B:
                return TransactionData.Status.ANN_P2B;
            case INV:
                return TransactionData.Status.INV;
            case PAG:
                return TransactionData.Status.PAG;
            case PND:
                return TransactionData.Status.PND;
            case RIC:
                return TransactionData.Status.RIC;
            case STR:
                return TransactionData.Status.STR;
            case ADD:
                return TransactionData.Status.ADD;
            case ATM:
                return TransactionData.Status.ATM;
            case POS:
                return TransactionData.Status.POS;
            case ANN_ATM:
                return TransactionData.Status.ANN_ATM;
            case ANN_POS:
                return TransactionData.Status.ANN_POS;
            case CASHBACK:
                return TransactionData.Status.CASHBACK;
            default:
                return null;
        }
    }


    public static Date getDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    private static Date getDateWithFormat(String date, SimpleDateFormat format) {
        try {
            return format.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static NotificationData getNotificationData(DtoGetPaymentRequestResponse requestResponse) {
        NotificationData notificationData = new NotificationData();

        ArrayList<NotificationPaymentData> notificationPaymentData = new ArrayList<>();
        if (requestResponse.getPaymentRequests() != null) {
            for (DtoPaymentRequest dtoPaymentRequest : requestResponse.getPaymentRequests()) {
                if (dtoPaymentRequest.getDtoP2BPaymentRequest() != null) {
                    notificationPaymentData.add(getNotificationPaymentData(dtoPaymentRequest.getDtoP2BPaymentRequest()));
                }
                if (dtoPaymentRequest.getDtoP2PPaymentRequest() != null) {
                    notificationPaymentData.add(getNotificationPaymentData(dtoPaymentRequest.getDtoP2PPaymentRequest()));
                }
            }
        }
        notificationData.setNotificationPaymentDatas(notificationPaymentData);

        ArrayList<BankIdRequest> bankIdRequests = new ArrayList<>();
        if (requestResponse.getBankIdRequests() != null) {
            for (DtoBankIdRequest item : requestResponse.getBankIdRequests()) {
                BankIdRequest request = getBankIdRequest(item);
                bankIdRequests.add(request);
            }
        }
        notificationData.setNotificationBankIdRequest(bankIdRequests);

        ArrayList<DirectDebitRequest> directDebitRequests = new ArrayList<>();
        if (requestResponse.getDirectDebitRequests() != null) {
            for (DtoDirectDebitRequest item : requestResponse.getDirectDebitRequests()) {
                DirectDebitRequest request = getDirectDebitRequest(item);
                directDebitRequests.add(request);
            }
        }
        notificationData.setNotificationDirectDebitRequests(directDebitRequests);

        return notificationData;
    }

    private static NotificationPaymentData getNotificationPaymentData(DtoP2BPaymentRequest dtoPaymentRequest) {
        NotificationPaymentData notificationPaymentData = new NotificationPaymentData();
        PaymentItem paymentItem = new PaymentItem();
        paymentItem.setAmount(BigDecimalUtils.getBigDecimalFromCents(dtoPaymentRequest.getAmount()));
        paymentItem.setCentsAmount(Integer.parseInt(dtoPaymentRequest.getAmount()));
        paymentItem.setCausal(dtoPaymentRequest.getCausal());
        paymentItem.setPaymentId(dtoPaymentRequest.getPaymentRequestId());
        paymentItem.setTillId(dtoPaymentRequest.getTillId());
        if (dtoPaymentRequest.getFee() != null) {
            paymentItem.setFee(BigDecimalUtils.getBigDecimalFromCents(dtoPaymentRequest.getFee()));
        }
        try {
            paymentItem.setType(PaymentItem.EPaymentRequestType.valueOf(dtoPaymentRequest.getType()));
        } catch (IllegalArgumentException e) {
            CustomLogger.e(TAG, "getNotificationPaymentData error: not recognized DtoP2BPaymentRequest type '"
                    + dtoPaymentRequest.getType() + "'");
            paymentItem.setType(PaymentItem.EPaymentRequestType.PAYMENT_REQUEST);
        }
        notificationPaymentData.setPaymentItem(paymentItem);
        notificationPaymentData.setItem(getShopItem(dtoPaymentRequest.getDtoShop()));
        return notificationPaymentData;
    }


    private static NotificationPaymentData getNotificationPaymentData(DtoP2PPaymentRequest dtoPaymentRequest) {
        NotificationPaymentData notificationPaymentData = new NotificationPaymentData();
        PaymentItem paymentItem = new PaymentItem();
        paymentItem.setInsignia(dtoPaymentRequest.getSenderName());
        paymentItem.setPaymentDate(getDate(dtoPaymentRequest.getPaymentRequestDate()));
        paymentItem.setAmount(BigDecimalUtils.getBigDecimalFromCents(dtoPaymentRequest.getAmount()));
        paymentItem.setCentsAmount(Integer.parseInt(dtoPaymentRequest.getAmount()));
        paymentItem.setCausal(dtoPaymentRequest.getCausal());
        paymentItem.setPaymentId(dtoPaymentRequest.getPaymentRequestId());
        paymentItem.setExpirationDate(getDate(dtoPaymentRequest.getExpirationDate()));
        notificationPaymentData.setPaymentItem(paymentItem);
        ContactItem contactItem = ApplicationModel.getInstance().getContactItem(dtoPaymentRequest.getMsisdnSender());
        if (contactItem == null) {
            contactItem = new ContactItem();
            contactItem.setMsisdn(dtoPaymentRequest.getMsisdnSender());
        }
        notificationPaymentData.setItem(contactItem);
        return notificationPaymentData;
    }

    public static ArrayList<InstrumentData> getInstruments(List<DtoInstrument> dtoInstruments) {
        ArrayList<InstrumentData> instruments = new ArrayList<>();
        if (dtoInstruments != null) {
            for (DtoInstrument dtoInstrument : dtoInstruments) {
                InstrumentData instrument = new InstrumentData();
                instrument.setDefaultIncoming(dtoInstrument.isDefaultIncoming());
                instrument.setDefaultOutgoing(dtoInstrument.isDefaultOutgoing());
                instrument.setIban(dtoInstrument.getIban());
                instrument.setCipheredIban(dtoInstrument.getCipheredIban());

                instruments.add(instrument);
            }
        }
        return instruments;
    }

    public static StatusCodeWrapper getStatusCodeWrapper(DtoStatus dtoStatus) {
        StatusCodeWrapper codeServer;
        try {
            if (!TextUtils.isEmpty(dtoStatus.getStatusDetailCode())) {
                codeServer = StatusCodeWrapper.valueOf("SCS_" + dtoStatus.getStatusCode() + "_" + dtoStatus.getStatusDetailCode());
            } else {
                codeServer = StatusCodeWrapper.valueOf("SCS_" + dtoStatus.getStatusCode());
            }

        } catch (Exception e) {
            codeServer = StatusCodeWrapper.SCS_9999_9999;
        }
        return codeServer;
    }

    public static StatusCodeInterface getStatusCodeInterface(DtoStatus dtoStatus) {
        StatusCodeWrapper codeServer = getStatusCodeWrapper(dtoStatus);
        return codeServer.getStatusCodeWrapped();
    }


    public static String getExtraMessage(DtoStatus dtoStatus) {
        if (dtoStatus.getStatusDetailCode() != null) {
            return dtoStatus.getStatusCode() + "$" + dtoStatus.getStatusDetailCode();
        } else {
            return dtoStatus.getStatusCode();
        }
    }

    public static PaymentRequestData getPaymentRequestData(DtoSendPaymentRequestResponse response) {
        PaymentRequestData data = new PaymentRequestData();
        data.setPaymentState(response.getPaymentRequestState());
        return data;
    }

    public static OutgoingPaymentRequestData getOutgoingPaymentData(DtoGetOutgoingPaymentRequestResponse response) {
        OutgoingPaymentRequestData data = new OutgoingPaymentRequestData();
        List<TransactionDataOutgoing> paymentRequests = new ArrayList<>();
        for (DtoP2PPaymentRequest item : response.getPaymentRequest()) {
            paymentRequests.add(getOutgoingPaymentItem(item));
        }
        data.setPaymentRequest(paymentRequests);
        return data;
    }

    private static TransactionDataOutgoing getOutgoingPaymentItem(DtoP2PPaymentRequest request) {
        TransactionDataOutgoing item = new TransactionDataOutgoing();

        item.setCausal(request.getCausal());
        item.setAmount(BigDecimalUtils.getBigDecimalFromCents(request.getAmount()));
        //item.setBeneficiaryName(request.getBeneficiaryName());
        item.setPaymentDate(getDateWithFormat(request.getExpirationDate(), new SimpleDateFormat("yyyy-MM-dd")));
        item.setMsisdn(request.getMsisdnBeneficiary());
        //item.setMsisdnSender(request.getMsisdnSender());
        item.setRequestDate(getDate(request.getPaymentRequestDate()));
        item.setTransactionId(request.getPaymentRequestId());
        item.setTransactionStatus(getTransactionStatusOutgoing(request.getPaymentRequestState()));
        item.setDisplayName(request.getSenderName());
        ContactItem contact = ApplicationModel.getInstance().getContactItem(request.getMsisdnBeneficiary());
        if (contact != null) {
            item.setImageResource(contact.getImage());
            item.setItemInterface(contact);
        } else {
            ContactItem contactItem = new ContactItem();
            contactItem.setMsisdn(request.getMsisdnBeneficiary());
            item.setItemInterface(contactItem);
        }
        item.setLetter(request.getLetter());
        item.setTransactionType(TransactionType.P2P);

        return item;
    }

    public static ArrayList<ContactItem> getBlockedContacts(DtoGetBlackListContactsResponse response) {
        ArrayList<ContactItem> list = new ArrayList<>();
        if (response.getBlackListMsidns() != null) {
            for (String msisdn : response.getBlackListMsidns()) {
                ContactItem contact = ApplicationModel.getInstance().getContactItem(msisdn);
                if (contact != null) {
                    contact.setBlocked(true);
                    list.add(contact);
                } else {
                    ContactItem unknownContact = new ContactItem();
                    unknownContact.setMsisdn(msisdn);
                    unknownContact.setBlocked(true);
                    list.add(unknownContact);
                }
            }
        }
        return list;
    }

    public static TransactionDetails getTransactionDetails(DtoGetTransactionDetailsResponse response) {
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setIban(response.getIban());
        return transactionDetails;
    }

    private static TransactionDataOutgoing.Status getTransactionStatusOutgoing(String status) {
        switch (status) {
            case "SENT":
                return TransactionDataOutgoing.Status.SENT;
            //Non arriva dal server
            /*case "ACCEPTED":
                return TransactionDataOutgoing.Status.ACCEPTED;*/
            case "EXPIRED":
                return TransactionDataOutgoing.Status.EXPIRED;
            case "FAILED":
                return TransactionDataOutgoing.Status.FAILED;
            default:
                return TransactionDataOutgoing.Status.UNKNOWN;
        }
    }

    public static BankServices getBankServiceList(List<String> serviceList) {
        BankServices bankServices = new BankServices();
        List<BankServices.EBankService> activeServices = new ArrayList<>();

        for (String item : serviceList) {
            try {
                activeServices.add(BankServices.EBankService.valueOf(item));
            } catch (IllegalArgumentException e) {
                CustomLogger.e(TAG,
                        "Received unknown String of bank service name, recognized values are P2P, P2B," +
                                " PAYMENT_REQUEST, LOYALTY_CARD, DOCUMENT, BASE, SPLIT_BILL");
            }
        }

        bankServices.setBankServiceList(activeServices);
        return bankServices;
    }

    public static LoyaltyCardsData getLoyaltyCardsData(DtoGetLoyaltyCardsResponse response) {
        LoyaltyCardsData loyaltyCardsData = new LoyaltyCardsData();
        List<LoyaltyCard> loyaltyCardList = new ArrayList<>();
        if (response.getDtoLoyaltyCards() != null) {
            for (DtoLoyaltyCard item : response.getDtoLoyaltyCards()) {
                loyaltyCardList.add(getLoyaltyCard(item));
            }
        }
        loyaltyCardsData.setLoyaltyCardList(loyaltyCardList);

        return loyaltyCardsData;
    }

    public static LoyaltyCard getLoyaltyCard(DtoLoyaltyCard response) {
        LoyaltyCard loyaltyCard = new LoyaltyCard();
        if (response != null) {
            loyaltyCard.setBarCodeNumber(response.getBarCodeNumber());
            loyaltyCard.setLoyaltyCardId(response.getLoyaltyCardId());
            loyaltyCard.setBarCodeType(response.getBarCodeType());
            loyaltyCard.setBrand(getLoyaltyBrand(response.getDtoBrand()));
        }

        return loyaltyCard;
    }

    public static LoyaltyCardBrandsData getLoyaltyCardBrandsData(DtoGetLoyaltyCardBrandsResponse response) {
        LoyaltyCardBrandsData loyaltyCardBrands = new LoyaltyCardBrandsData();
        List<LoyaltyBrand> brandList = new ArrayList<>();
        if (response.getDtoBrands() != null) {
            for (DtoBrand dtoBrand : response.getDtoBrands()) {
                brandList.add(getLoyaltyBrand(dtoBrand));
            }
            loyaltyCardBrands.setBrandList(brandList);
        }

        return loyaltyCardBrands;
    }

    private static LoyaltyBrand getLoyaltyBrand(DtoBrand brandResponse) {
        LoyaltyBrand brand = new LoyaltyBrand();
        brand.setBrandName(brandResponse.getBrandName());
        brand.setBrandUuid(brandResponse.getBrandUuid());
        if (!TextUtils.isEmpty(brandResponse.getHexColor())) {
            try {
                brand.setCardColor(Color.parseColor(brandResponse.getHexColor()));
            } catch (Exception e) {
                brand.setCardColor(ContextCompat.getColor(PayCore.getAppContext(), R.color.colorAccent));
            }
        } else {
            brand.setCardColor(ContextCompat.getColor(PayCore.getAppContext(), R.color.colorAccent));
        }
        brand.setCardImage(brandResponse.getBrandLogoImage());
        brand.setLight(brandResponse.isLight());
        brand.setCardLogoUrl(brandResponse.getBrandLogoUrl());
        brand.setCardType(brandResponse.getType());

        return brand;
    }

    public static String getLoyaltyCardId(DtoSetLoyaltyCardResponse response) {
        String loyaltyCardId = "";
        if (!TextUtils.isEmpty(response.getLoyaltyCardId())) {
            loyaltyCardId = response.getLoyaltyCardId();
        }
        return loyaltyCardId;
    }

    public static DocumentsData getDocumentsData(List<DtoDocument> documentList) {
        DocumentsData documentsData = new DocumentsData();
        List<BcmDocument> bcmDocumentList = new ArrayList<>();
        if (documentList != null) {
            for (DtoDocument item : documentList) {
                BcmDocument document = new BcmDocument();
                document.setDocumentUuid(item.getDocumentUuid());
                try {
                    document.setDocumentType(DtoDocument.DocumentTypeEnum.valueOf(item.getDocumentType()));
                } catch (IllegalArgumentException e) {
                    document.setDocumentType(DtoDocument.DocumentTypeEnum.OTHER);
                }
                document.setDocumentNumber(item.getDocumentNumber());
                document.setSurname(item.getSurname());
                document.setName(item.getName());
                document.setFiscalCode(item.getFiscalCode());
                document.setIssuingInstitution(item.getIssuingInstitution());
                document.setIssuingDate(item.getIssuingDate());
                document.setExpirationDate(item.getExpirationDate());
                document.setNote(item.getNote());
                document.setDocumentName(item.getDocumentName());

                bcmDocumentList.add(document);
            }
        }
        documentsData.setDocumentList(bcmDocumentList);
        return documentsData;
    }

    public static BcmDocument modifyDocumentData(DtoModifyDocumentResponse modifiedDocumentData) {
        BcmDocument documentData = new BcmDocument();
        documentData.setDocumentUuid(modifiedDocumentData.getDocumentUuid());
        try {
            documentData.setDocumentType(modifiedDocumentData.getDocumentType());
        } catch (IllegalArgumentException e) {
            documentData.setDocumentType(DtoDocument.DocumentTypeEnum.OTHER);
        }
        documentData.setDocumentNumber(modifiedDocumentData.getDocumentNumber());
        documentData.setSurname(modifiedDocumentData.getSurname());
        documentData.setName(modifiedDocumentData.getName());
        documentData.setFiscalCode(modifiedDocumentData.getFiscalCode());
        documentData.setIssuingInstitution(modifiedDocumentData.getIssuingInstitution());
        documentData.setIssuingDate(modifiedDocumentData.getIssuingDate());
        documentData.setExpirationDate(modifiedDocumentData.getExpirationDate());
        documentData.setNote(modifiedDocumentData.getNote());
        documentData.setDocumentName(modifiedDocumentData.getDocumentName());

        return documentData;
    }


    public static DocumentImages getDocumentImages(DtoGetDocumentImagesResponse documentImagesResponse) {
        DocumentImages documentImages = new DocumentImages();
        documentImages.setFrontImage(documentImagesResponse.getFrontImage());
        documentImages.setBackImage(documentImagesResponse.getBackImage());
        return documentImages;
    }

    public static ArrayList<BankIdMerchantData> getBankIdMerchantData(DtoGetBankIdBlacklistResponse response) {
        ArrayList<BankIdMerchantData> merchantList = new ArrayList<>();
        if (response.getDtoBankIdMerchants() != null) {
            for (DtoBankIdMerchant item : response.getDtoBankIdMerchants()) {
                BankIdMerchantData merchantData = new BankIdMerchantData();
                merchantData.setMerchantTag(item.getMerchantTag());
                merchantData.setMerchantName(item.getBusinessName());
                merchantList.add(merchantData);
            }
        }
        return merchantList;
    }

    public static BankIdContactsData getBankIdContactsData(DtoGetBankIdContactsResponse response) {
        BankIdContactsData contactsData = new BankIdContactsData();
        contactsData.setEmail(response.getEmail());

        List<BankIdAddress> bankIdAddresses = new ArrayList<>();

        if (response.getDtoBankIdAddresses() != null) {
            for (DtoBankIdAddress item : response.getDtoBankIdAddresses()) {

                BankIdAddress bankIdAddress = new BankIdAddress();
                Address address = getAddress(item.getDtoAddress());
                bankIdAddress.setAddress(address);
                bankIdAddress.setCareOf(item.getCareOf());
                bankIdAddress.setCountry(item.getCountry());
                if (item.isBillingAddress() && item.isShippingAddress()) {
                    bankIdAddress.setAddressType(BankIdAddress.EBankIdAddressType.BOTH);
                } else if (item.isBillingAddress()) {
                    bankIdAddress.setAddressType(BankIdAddress.EBankIdAddressType.BILLING);
                } else if (item.isShippingAddress()) {
                    bankIdAddress.setAddressType(BankIdAddress.EBankIdAddressType.SHIPPING);
                }
                bankIdAddress.setDefaultBillingAddress(item.isDefaultBillingAddress());
                bankIdAddress.setDefaultShippingAddress(item.isDefaultShippingAddress());

                bankIdAddresses.add(bankIdAddress);
            }

            contactsData.setBankIdAddress(bankIdAddresses);
        }

        return contactsData;
    }

    public static List<DtoBankIdAddress> getBankIdDtoAddresses(List<BankIdAddress> bankIdAddresses) {
        List<DtoBankIdAddress> dtoBankIdAddresses = new ArrayList<>();

        if (bankIdAddresses != null) {
            for (BankIdAddress item : bankIdAddresses) {
                DtoBankIdAddress dtoBankIdAddress = new DtoBankIdAddress();
                dtoBankIdAddress.setCareOf(item.getCareOf());
                dtoBankIdAddress.setCountry(item.getCountry());
                DtoAddress dtoAddress = new DtoAddress();
                if (item.getAddress() != null) {
                    dtoAddress.setCity(item.getAddress().getCity());
                    dtoAddress.setPostalCode(item.getAddress().getPostalCode());
                    dtoAddress.setProvince(item.getAddress().getProvince());
                    dtoAddress.setStreet(item.getAddress().getStreet());
                }
                dtoBankIdAddress.setDtoAddress(dtoAddress);
                if (item.getAddressType() == BankIdAddress.EBankIdAddressType.BOTH) {
                    dtoBankIdAddress.setBillingAddress(true);
                    dtoBankIdAddress.setShippingAddress(true);
                } else if (item.getAddressType() == BankIdAddress.EBankIdAddressType.BILLING) {
                    dtoBankIdAddress.setBillingAddress(true);
                    dtoBankIdAddress.setShippingAddress(false);
                } else if (item.getAddressType() == BankIdAddress.EBankIdAddressType.SHIPPING) {
                    dtoBankIdAddress.setBillingAddress(false);
                    dtoBankIdAddress.setShippingAddress(true);
                }
                dtoBankIdAddress.setDefaultBillingAddress(item.isDefaultBillingAddress());
                dtoBankIdAddress.setDefaultShippingAddress(item.isDefaultShippingAddress());

                dtoBankIdAddresses.add(dtoBankIdAddress);
            }
        }

        return dtoBankIdAddresses;
    }

    public static AtmConfirmWithdrawalData getAtmConfirmWithdrawalData(DtoConfirmAtmWithdrawalResponse response) {
        AtmConfirmWithdrawalData atmWithdrawalData = new AtmConfirmWithdrawalData();
        atmWithdrawalData.setWithdrawalState(response.getAtmWithdrawalState());
        return atmWithdrawalData;
    }

    public static PosConfirmWithdrawalData getPosConfirmWithdrawalData(DtoConfirmPosWithdrawalResponse response) {
        PosConfirmWithdrawalData posWithdrawalData = new PosConfirmWithdrawalData();
        posWithdrawalData.setWithdrawalState(response.getPosWithdrawalState());
        return posWithdrawalData;
    }

    public static LoyaltyJwtData getLoyaltyJwtData(DtoGetLoyaltyJwtResponse response) {
        LoyaltyJwtData loyaltyJwtData = new LoyaltyJwtData();
        loyaltyJwtData.setJwt(response.getJwt());
        loyaltyJwtData.setCallbackUrl(response.getCallbackUrl());
        loyaltyJwtData.setBplayLoyaltyUrl(response.getBplayLoyaltyUrl());
        loyaltyJwtData.setWhitelistUrls(response.getWhitelistUrls());
        return loyaltyJwtData;
    }

    public static String getStatusCodeMessage(DtoStatus dtoStatus) {
        String statusCodeMessage;
        String sCompleteStatus = "SCS_" + dtoStatus.getStatusCode();
        if (!TextUtils.isEmpty(dtoStatus.getStatusDetailCode())) {
            sCompleteStatus += "_" + dtoStatus.getStatusDetailCode();
        }

        try {

            StatusCodeWrapper errorWrapper = StatusCodeWrapper.valueOf(sCompleteStatus);

            switch (errorWrapper) {

                case SCS_0000:
                    statusCodeMessage = "Ok";
                    break;
                case SCS_0001_0000:
                    statusCodeMessage = "Generic Server Error";
                    break;
                case SCS_0001_0020:
                    statusCodeMessage = "Format MSISDN is not valid";
                    break;
                case SCS_0001_0022:
                    statusCodeMessage = "Generic Qr Code Server Error ";
                    break;
                case SCS_0002_0008:
                    statusCodeMessage = "Reset for security reason";
                    break;
                case SCS_0002_0012:
                    statusCodeMessage = "Bank not found";
                    break;
                case SCS_0002_0014:
                    statusCodeMessage = "Version needs to be updated";
                    break;
                case SCS_0002_0099:
                    statusCodeMessage = "Payment request denied: Impossible to block contact.";
                    break;
                case SCS_0003_0000:
                    statusCodeMessage = "P2P generic error";
                    break;
                case SCS_0003_0006:
                    statusCodeMessage = "P2P verify payment failed";
                    break;
                case SCS_0003_0018:
                    statusCodeMessage = "P2P user not enabled on BCM Pay";
                    break;
                case SCS_0003_0022:
                    statusCodeMessage = "P2P receiver account not valid";
                    break;
                case SCS_0003_0023:
                    statusCodeMessage = "P2P amount not valid";
                    break;
                case SCS_0003_0024:
                    statusCodeMessage = "P2P failed missing payment request";
                    break;
                case SCS_0003_0027:
                    statusCodeMessage = "P2P description contains special character";
                    break;
                case SCS_0003_0032:
                    statusCodeMessage = "P2P request cannot be denied";
                    break;
                case SCS_0003_0036:
                    statusCodeMessage = "P2P failed: request to not valid receiver";
                    break;
                case SCS_0003_0052:
                    statusCodeMessage = "You have exceeded the maximum number of participants for a bill split. Reduce the number of participants and try again";
                    break;
                case SCS_0004_0003:
                    statusCodeMessage = "MSISDN is not valid";
                    break;
                case SCS_0004_0004:
                    statusCodeMessage = "OTP is not valid";
                    break;
                case SCS_0004_0005:
                    statusCodeMessage = "OTP expired";
                    break;
                case SCS_0004_0006:
                    statusCodeMessage = "Max number of OTP requests reached ";
                    break;
                case SCS_0004_0000:
                    statusCodeMessage = "Generic external server error";
                    break;
                case SCS_0006_0000:
                    statusCodeMessage = "Generic QrCode error";
                    break;
                case SCS_0006_0005:
                    statusCodeMessage = "QrCode is not valid";
                    break;
                case SCS_0007_0005:
                    statusCodeMessage = "P2B merchant not found";
                    break;
                case SCS_0007_0006:
                    statusCodeMessage = "P2B amount is not valid";
                    break;
                case SCS_0007_0007:
                    statusCodeMessage = "P2B daily threshold reached";
                    break;
                case SCS_0007_0008:
                    statusCodeMessage = "P2B monthly threshold reached";
                    break;
                case SCS_0007_0009:
                    statusCodeMessage = "P2B QrCode no more valid";
                    break;
                case SCS_0007_0014:
                    statusCodeMessage = "P2B Failed to retrieve payment";
                    break;
                case SCS_0007_0015:
                    statusCodeMessage = "P2B shop list search failed";
                    break;
                case SCS_0007_0019:
                    statusCodeMessage = "P2B shop list search failed: MSISDN not valid";
                    break;
                case SCS_0007_0052:
                    statusCodeMessage = "No more iban active on this account";
                    break;
                case SCS_0008_0002:
                    statusCodeMessage = "Oauth refresh token expired";
                    break;
                case SCS_0010_0000:
                    statusCodeMessage = "Other generic server Error";
                    break;
                /*case SCS_0010_0001:
                    statusCodeMessage = "Payment not available for the selected shop";
                    break;*/
                case SCS_0014_0001:
                    statusCodeMessage = "Loyalty card already registered";
                    break;
                case SCS_0016_0001:
                    statusCodeMessage = "BankId access request expired";
                    break;
                case SCS_0001_0001:
                case SCS_9999_9999:
                    statusCodeMessage = "Generic Mobile Server Error";
                    break;

                default:
                    return "Generic error";
            }

        } catch (IllegalArgumentException e) {
            CustomLogger.w(TAG, "getStatusCodeMessage threw IllegalArgumentException");
            return "Generic error";
        }

        return statusCodeMessage;

    }

    public static ArrayList<BankIdMerchantData> getBankIdMerchantDataList(DtoGetBankIdBlacklistResponse dtoResponse) {
        ArrayList<BankIdMerchantData> merchantList = new ArrayList<>();
        if (dtoResponse.getDtoBankIdMerchants() != null) {
            for (DtoBankIdMerchant item : dtoResponse.getDtoBankIdMerchants()) {
                BankIdMerchantData merchantData = new BankIdMerchantData();
                merchantData.setMerchantTag(item.getMerchantTag());
                merchantData.setMerchantName(item.getBusinessName());
                merchantList.add(merchantData);
            }
        }
        return merchantList;
    }

    public static BankIdRequestsData getBankIdRequestsData(DtoGetBankIdRequestsResponse response) {
        BankIdRequestsData bankIdRequestsData = new BankIdRequestsData();
        List<BankIdRequest> bankIdRequestsList = new ArrayList<>();
        if (response.getDtoBankIdRequests() != null) {
            for (DtoBankIdRequest item : response.getDtoBankIdRequests()) {
                bankIdRequestsList.add(getBankIdRequest(item));
            }
        }
        bankIdRequestsData.setBankIdRequestsList(bankIdRequestsList);
        return bankIdRequestsData;
    }

    public static DirectDebitsHistoryData getDirectDebitsData(List<DtoDirectDebitHistoryElement> directDebitHistoryElementsList) {
        DirectDebitsHistoryData directDebitsHistoryData = new DirectDebitsHistoryData();
        ArrayList<DirectDebitHistoryElement> directDebitHistoryElementList = new ArrayList<>();
        if (directDebitHistoryElementsList != null) {
            for (DtoDirectDebitHistoryElement item : directDebitHistoryElementsList) {
                directDebitHistoryElementList.add(getDirectDebutHistoryElement(item));
            }
        }
        directDebitsHistoryData.setDirectDebitHistoryElementList(directDebitHistoryElementList);
        return directDebitsHistoryData;
    }

    private static BankIdRequest getBankIdRequest(DtoBankIdRequest response) {
        BankIdRequest bankIdRequest = new BankIdRequest();
        if (response != null) {
            bankIdRequest.setRequestId(response.getRequestId());
            bankIdRequest.setRequestDateTime(getDate(response.getRequestDateTime()));
            bankIdRequest.setBankIdMerchantData(getBankIdMerchantData(response.getDtoBankIdMerchant()));
        }
        return bankIdRequest;
    }

    private static DirectDebitRequest getDirectDebitRequest(DtoDirectDebitRequest request) {
        DirectDebitRequest directDebitRequest = new DirectDebitRequest();
        if (request != null) {
            directDebitRequest.setRequestId(request.getRequestId());
            directDebitRequest.setRequestDateTime(getDate(request.getRequestDateTime()));
            directDebitRequest.setMerchantName(request.getMerchantName());
            directDebitRequest.setTag(request.getTag());
        }
        return directDebitRequest;
    }

    private static DirectDebitHistoryElement getDirectDebutHistoryElement(DtoDirectDebitHistoryElement response) {
        DirectDebitHistoryElement directDebitHistoryElement = new DirectDebitHistoryElement();
        if (response != null) {
            directDebitHistoryElement.setDirectDebitStatus(response.getDirectDebitStatus());
            directDebitHistoryElement.setStartingDate(getDate(response.getStartingDate()));
            directDebitHistoryElement.setEndingDate(getDate(response.getEndingDate()));
            directDebitHistoryElement.setAuthorizationDate(getDate(response.getAuthorizationDate()));
            directDebitHistoryElement.setIban(response.getIban());
            directDebitHistoryElement.setMerchantName(response.getMerchantName());
            directDebitHistoryElement.setDirectDebitDescription(response.getDirectDebitDescription());
        }
        return directDebitHistoryElement;
    }

    private static BankIdMerchantData getBankIdMerchantData(DtoBankIdMerchant bankIdResponse) {
        BankIdMerchantData bankIdMerchant = new BankIdMerchantData();
        bankIdMerchant.setMerchantTag(bankIdResponse.getMerchantTag());
        bankIdMerchant.setMerchantName(bankIdResponse.getBusinessName());
        return bankIdMerchant;
    }

    public static SyncPhoneBookData getSyncPhoneBookData(ArrayList<ContactItem> contactItems, boolean isContactsSynced) {
        SyncPhoneBookData syncPhoneBookData = new SyncPhoneBookData();
        syncPhoneBookData.setContactsSynced(isContactsSynced);
        syncPhoneBookData.setContactItems(contactItems);
        return syncPhoneBookData;
    }

    public static CashbackStatusData getCashbackStatusData(DtoGetCashbackStatusResponse response) {
        CashbackStatusData data = new CashbackStatusData();
        data.setbPaySubscribed(response.isBpaySubscribed());
        data.setbPaySubscribedTimestamp(response.getBpaySubscribedTimestamp());
        data.setbPayUnsubscribedTimestamp(response.getBpayUnsubscribedTimestamp());
        data.setbPayTermsAndConditionsAccepted(response.isBpayTermsAndConditionsAccepted());
        data.setPagoPaCashbackEnabled(response.isPagoPaCashbackEnabled());
        data.setPagoPaTermsAndConditionsUrl(response.getPagoPaTermsAndConditionsUrl());
        data.setBpayTermsAndConditionsUrl(response.getBpayTermsAndConditionsUrl());
        return data;
    }

    public static CashbackData getCashbackData(DtoGetCashbackDataResponse response) {
        CashbackData cashbackData = new CashbackData();
        cashbackData.setCashback(response.getCashback());
        cashbackData.setTransactionNumber(response.getTransactionsNumber());
        cashbackData.setMinTransactionNumber(response.getMinTransactionsNumber());
        cashbackData.setMaxTransactionNumber(response.getMaxTransactionsNumber());
        cashbackData.setRanking(response.getRanking());
        cashbackData.setPeriodStartDate(response.getPeriodStartDate());
        cashbackData.setPeriodEndDate(response.getPeriodEndDate());
        cashbackData.setParticipantsNumber(response.getParticipantsNumber());
        return cashbackData;
    }

    public static DtoCustomerJourneyTag getCustomerJourneyTag(CustomerJourneyTag item) {
        DtoCustomerJourneyTag tag = new DtoCustomerJourneyTag();
        tag.setTimestamp(item.getTagTimestamp());
        tag.setExecutionId(item.getTagExecutionId());
        tag.setEvent(item.getTagKey());
        tag.setParams(item.getTagJsonData());
        tag.setCuid(item.getCuid());
        return tag;
    }

    public static List<DtoCustomerJourneyTag> getCustomerJourneyTagsRequest(List<CustomerJourneyTag> savedTags) {
        List<DtoCustomerJourneyTag> tags = new ArrayList<>();
        for (CustomerJourneyTag item : savedTags) {
            DtoCustomerJourneyTag tag = getCustomerJourneyTag(item);
            tags.add(tag);
        }
        return tags;
    }


    public static List<SplitBillHistory> getSplitBillHistory(DtoSplitBillHistoryResponse dtoSplitBillHistoryResponse) {
        List<SplitBillHistory> splitBillHistory = new ArrayList<>();
        for(DtoSplitBill dtoSplitBill : dtoSplitBillHistoryResponse.getSplitBills()){
            SplitBillHistory historyItem = new SplitBillHistory();
            historyItem.setSplitBillUUID(dtoSplitBill.getSplitBillUUID());
            historyItem.setRequestDate(dtoSplitBill.getRequestDate());
            historyItem.setCausal(dtoSplitBill.getCausal());
            historyItem.setAmount(dtoSplitBill.getAmount());
            historyItem.setDescription(dtoSplitBill.getDescription());
            splitBillHistory.add(historyItem);
        }
        return splitBillHistory;
    }

    public static List<SplitBeneficiary> getSplitBillDetail(DtoSplitBillHistoryDetailResponse dtoSplitBillHistoryDetailResponse) {
        List<SplitBeneficiary> splitBeneficiaries = new ArrayList<>();
        for(DtoSplitBillDetail dtoSplitBillDetail : dtoSplitBillHistoryDetailResponse.getSplitBillDetails()){
            SplitBeneficiary beneficiary = new SplitBeneficiary();
            ContactItem contactItem = new ContactItem();
            contactItem.setMsisdn(dtoSplitBillDetail.getMsisdn());
            beneficiary.setBeneficiary(contactItem);
            beneficiary.setAmount(dtoSplitBillDetail.getAmount());
            beneficiary.setSplitBillState(SplitBeneficiary.Status.valueOf(dtoSplitBillDetail.getStatus()));
            splitBeneficiaries.add(beneficiary);
        }
        return splitBeneficiaries;
    }

}
