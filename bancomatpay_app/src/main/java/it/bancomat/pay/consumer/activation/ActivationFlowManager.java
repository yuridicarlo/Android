package it.bancomat.pay.consumer.activation;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

import it.bancomat.pay.consumer.activation.activities.ActivableBankListActivity;
import it.bancomat.pay.consumer.activation.activities.ActivationActivity;
import it.bancomat.pay.consumer.activation.activities.ActivationCompletedActivity;
import it.bancomat.pay.consumer.activation.activities.ActivationCompletedSdkErrorActivity;
import it.bancomat.pay.consumer.activation.activities.NoBcmPayBankListActivity;
import it.bancomat.pay.consumer.activation.activities.NotBcmPayUserActivity;
import it.bancomat.pay.consumer.activation.activities.SetFingerprintActivity;
import it.bancomat.pay.consumer.activation.activities.SetFingerprintSuccessActivity;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.init.InitActivity;
import it.bancomat.pay.consumer.network.dto.ActivationData;
import it.bancomat.pay.consumer.storeLocator.StoreLocatorActivity;

public class ActivationFlowManager {

	public static final String BANK_DATA = "BANK_DATA";
	public static final String ACTIVATION_DATA = "ACTIVATION_DATA";
	public static final String BANK_UUID = "BANK_UUID";
	public static final String ACTIVATION_CODE = "ACTIVATION_CODE";
	public static final String TOKEN = "TOKEN";
	public static final String ACTIVATION_FROM_DEEP_LINK = "ACTIVATION_FROM_DEEP_LINK";
	public static final String BANK_LABEL = "BANK_LABEL";
	public static final String PENDING_PAYMENTS = "PENDING_PAYMENTS";
	public static final String USER_DATA = "USER_DATA";
	public static final String PENDING_PAYMENT_STATUS = "PENDING_PAYMENT_STATUS";
	public static final String FORCE_REOPEN_BANCOMAT_FLOW = "FORCE_REOPEN_BANCOMAT_FLOW";

	public static void goToActivableBankList(Activity activity) {
		Intent i = new Intent(activity, ActivableBankListActivity.class);
		activity.startActivity(i);
	}

	public static void goToActivation(Activity activity, DataBank dataBank) {
		Intent i = new Intent(activity, ActivationActivity.class);
		i.putExtra(BANK_DATA, dataBank);
		activity.startActivity(i);
	}

	public static void goToNoBcmPayBankList(Activity activity) {
		Intent i = new Intent(activity, NoBcmPayBankListActivity.class);
		activity.startActivity(i);
	}

	public static void goToIntro(Activity activity) {
		Intent i = new Intent(activity, InitActivity.class);
		//Intent i = new Intent(activity, IntroActivity.class);
		activity.startActivity(i);
		activity.finishAffinity();
	}

	public static void goToChooseActivationModeDeepLink(Activity activity, DataBank bankData, String token, String activationCode) {
		Intent i = new Intent(activity, InitActivity.class);
		//Intent i = new Intent(activity, ActivationActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		//i.putExtra(BANK_DATA, bankData);
		//i.putExtra(TOKEN, token);
		i.putExtra(ACTIVATION_CODE, activationCode);
		i.putExtra(ACTIVATION_FROM_DEEP_LINK, true);
		activity.startActivity(i);
		activity.finish();
	}

	public static void goToSetFingerprint(Activity activity, boolean forceReopenBancomatFlow, String bankUUID) {
		Intent i = new Intent(activity, SetFingerprintActivity.class);
		i.putExtra(FORCE_REOPEN_BANCOMAT_FLOW, forceReopenBancomatFlow);
		i.putExtra(BANK_UUID, bankUUID);
		activity.startActivity(i);
	}

	public static void goToActivationCompleted(Activity activity, ActivationData activationData, String bankUUID) {
		Intent i = new Intent(activity, ActivationCompletedActivity.class);
		i.putExtra(ACTIVATION_DATA, activationData);
		i.putExtra(BANK_UUID, bankUUID);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(i);
		activity.finish();
	}

	public static void goToSetFingerprintSuccess(Activity activity, boolean forceReopenBancomatFlow) {
		Intent i = new Intent(activity, SetFingerprintSuccessActivity.class);
		i.putExtra(FORCE_REOPEN_BANCOMAT_FLOW, forceReopenBancomatFlow);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(i);
		activity.finish();
	}

	public static void goToActivationCompletedSdkError(Activity activity, ActivationData activationData, String bankUUID) {
		Intent i = new Intent(activity, ActivationCompletedSdkErrorActivity.class);
		i.putExtra(ACTIVATION_DATA, activationData);
		i.putExtra(BANK_UUID, bankUUID);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(i);
		activity.finish();
	}

	public static void goToNoBankAvailable(Activity activity, String bankLabel) {
		Intent i = new Intent(activity, NotBcmPayUserActivity.class);
		i.putExtra(BANK_LABEL, bankLabel);
		activity.startActivity(i);
	}

	public static void goToPlayStore(Activity activity, DataBank bank) {
		Intent i = new Intent(android.content.Intent.ACTION_VIEW);
		i.setData(Uri.parse(bank.getLinkStore()));
		activity.startActivity(i);
	}

	public static void goToShowTermsAndConditions(Activity activity, String url) {
		try {
			CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
			builder.setShareState(CustomTabsIntent.SHARE_STATE_ON);
			CustomTabsIntent customTabsIntent = builder.build();
			customTabsIntent.launchUrl(activity, Uri.parse(url));
		} catch (ActivityNotFoundException ignored) {
		}
	}

    public static void goToStoreLocator(Activity activity) {
		Intent intent = new Intent(activity, StoreLocatorActivity.class);
		activity.startActivity(intent);
    }
}
