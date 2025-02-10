package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdk.manager.task.model.CashbackStatusData;
import it.bancomatpay.sdk.manager.task.model.LoyaltyJwtData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdkui.activities.BPlayActivity;
import it.bancomatpay.sdkui.activities.BankServiceTutorialActivity;
import it.bancomatpay.sdkui.activities.ContactsActivity;
import it.bancomatpay.sdkui.activities.NotificationListActivity;
import it.bancomatpay.sdkui.activities.ProfileActivity;
import it.bancomatpay.sdkui.activities.SettingsActivity;
import it.bancomatpay.sdkui.activities.TransactionListActivity;
import it.bancomatpay.sdkui.activities.atmcardless.AtmCardlessScanQrCodeActivity;
import it.bancomatpay.sdkui.activities.bankid.BankIdInfoActivity;
import it.bancomatpay.sdkui.activities.cashaback.CashbackConfirmActivationActivity;
import it.bancomatpay.sdkui.activities.cashaback.CashbackDetailActivity;
import it.bancomatpay.sdkui.activities.directdebit.DirectDebitMerchantListActivity;
import it.bancomatpay.sdkui.activities.documents.DocumentListActivity;
import it.bancomatpay.sdkui.activities.loyaltycard.LoyaltyCardListActivity;
import it.bancomatpay.sdkui.activities.petrol.PetrolChoosePumpActivity;
import it.bancomatpay.sdkui.activities.split_bill.SplitBillActivity;
import it.bancomatpay.sdkui.model.PaymentContactFlowType;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.flowmanager.PetrolFlowManager.PETROL_MERCHANT_DATA;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_ATM_CARDLESS;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_BANK_ID;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_DOCUMENTS;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_LOYALTY_CARDS;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_PETROL;

public class HomeFlowManager {

    public static final String TUTORIAL_BANK_SERVICE = "TUTORIAL_BANK_SERVICE";
    public static final String LOYALTY_JWT_DATA = "LOYALTY_JWT_DATA";
    public static final String CASHBACK_SHOW_INFO_DIALOG = "CASHBACK_SHOW_INFO_DIALOG";
    public static final String CASHBACK_STATUS_DATA_EXTRA = "CASHBACK_BPAY_TERMS_AND_CONDITIONS_EXTRA";
    public static final String SPLIT_BILL_CREATE_NEW_EXTRA = "SPLIT_BILL_CREATE_NEW_EXTRA";
    public static final String SPLIT_BILL_DETAIL_EXTRA = "SPLIT_BILL_DETAIL_EXTRA";


    public static void goToNotificationsNoAnimation(Activity activity) {
        Intent intent = new Intent(activity, NotificationListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }

    public static void goToNotifications(Activity activity) {
        Intent intent = new Intent(activity, NotificationListActivity.class);
        activity.startActivity(intent);
    }

    public static void goToContacts(Activity activity, PaymentContactFlowType flowType) {
        Intent intent = new Intent(activity, ContactsActivity.class);
        intent.putExtra(ContactsActivity.PAYMENT_CONTACT_FLOW_TYPE, flowType);
        activity.startActivity(intent);
    }

    public static void goToTransactions(Activity activity) {
        Intent intent = new Intent(activity, TransactionListActivity.class);
        activity.startActivity(intent);
    }

    public static void goToProfile(Activity activity) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        activity.startActivity(intent);
    }

    public static void goToSettings(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    public static void goToLoyaltyCards(Activity activity) {
        Intent intent;
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED && !FullStackSdkDataManager.getInstance().isTutorialLoyaltyCardsAlreadyShown()) {
            intent = new Intent(activity, BankServiceTutorialActivity.class);
            intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_LOYALTY_CARDS);
        } else {
            intent = new Intent(activity, LoyaltyCardListActivity.class);
        }
        activity.startActivity(intent);
    }

    public static void goToDocuments(Activity activity) {
        Intent intent;
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED && !FullStackSdkDataManager.getInstance().isTutorialDocumentsAlreadyShown()) {
            intent = new Intent(activity, BankServiceTutorialActivity.class);
            intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_DOCUMENTS);
        } else {
            intent = new Intent(activity, DocumentListActivity.class);
        }
        activity.startActivity(intent);
    }

    public static void goToBankId(Activity activity) {
        Intent intent;
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED && !FullStackSdkDataManager.getInstance().isTutorialBankIdAlreadyShown()) {
            intent = new Intent(activity, BankServiceTutorialActivity.class);
            intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_BANK_ID);
        } else {
            intent = new Intent(activity, BankIdInfoActivity.class);
        }
        activity.startActivity(intent);
    }

    public static void goToAtmCardless(Activity activity) {
        Intent intent;
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED && !FullStackSdkDataManager.getInstance().isTutorialAtmCardlessAlreadyShown()) {
            intent = new Intent(activity, BankServiceTutorialActivity.class);
            intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_ATM_CARDLESS);
        } else {
            intent = new Intent(activity, AtmCardlessScanQrCodeActivity.class);
        }
        activity.startActivity(intent);
    }

    public static void goToPetrol(Activity activity, ShopItem shopItem) {
        Intent intent;
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED && !FullStackSdkDataManager.getInstance().isTutorialPetrolAlreadyShown()) {
            intent = new Intent(activity, BankServiceTutorialActivity.class);
            intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_PETROL);
        } else {
            intent = new Intent(activity, PetrolChoosePumpActivity.class);
        }
        intent.putExtra(PETROL_MERCHANT_DATA, shopItem);
        activity.startActivity(intent);
    }

    public static void goToBplay(Activity activity, LoyaltyJwtData loyaltyJwtData) {
        Intent intent = new Intent(activity, BPlayActivity.class);
        intent.putExtra(LOYALTY_JWT_DATA, loyaltyJwtData);
        activity.startActivity(intent);
    }

    public static void goToCashbackDigitalPayments(Activity activity, CashbackStatusData cashbackStatusData) {
        Intent intent = new Intent(activity, CashbackDetailActivity.class);
        intent.putExtra(CASHBACK_STATUS_DATA_EXTRA, cashbackStatusData);
        activity.startActivity(intent);
    }

    public static void goToCashbackTermsAndConditions(Activity activity, CashbackStatusData cashbackStatusData) {
        Intent intent = new Intent(activity, CashbackConfirmActivationActivity.class);
        intent.putExtra(CASHBACK_STATUS_DATA_EXTRA, cashbackStatusData);
        activity.startActivity(intent);
    }

    public static void goToSplitBill(Activity activity) {
        Intent intent = new Intent(activity, SplitBillActivity.class);
        activity.startActivity(intent);
    }

    public static void goToNewSplitBill(Activity activity) {
        Intent intent = new Intent(activity, SplitBillActivity.class);
        intent.putExtra(SPLIT_BILL_CREATE_NEW_EXTRA, true);
        activity.startActivity(intent);
    }

    public static void goToSplitBill(Activity activity, SplitBillHistory item) {
        Intent intent = new Intent(activity, SplitBillActivity.class);
        intent.putExtra(SPLIT_BILL_DETAIL_EXTRA, item);

        activity.startActivity(intent);
    }

    public static void goToDirectDebits(Activity activity) {
        Intent intent = new Intent(activity, DirectDebitMerchantListActivity.class);
        activity.startActivity(intent);
    }

}
