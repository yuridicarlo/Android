package it.bancomatpay.sdkui.utilities;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class AlertDialogBuilderExtended extends AlertDialog.Builder {

	public AlertDialogBuilderExtended(@NonNull Context context) {
		super(context);
	}

	public void showDialog(Activity activity) {
		if (!activity.isFinishing() && !activity.isDestroyed()) {
			super.show();
		}
	}

}
