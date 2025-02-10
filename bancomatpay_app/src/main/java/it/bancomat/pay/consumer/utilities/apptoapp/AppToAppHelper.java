package it.bancomat.pay.consumer.utilities.apptoapp;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class AppToAppHelper {

    private static final String ACTIVATION_CODE = "#activationCode";

    public static Result<AppToAppData> getActivationCodeIfExist(Intent intent) {
        Result<AppToAppData> result = new Result<>();

        String input = "";
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            input = (data.getQueryParameter(ACTIVATION_CODE));
        }
        if (TextUtils.isEmpty(input)) {
            result.setStatusCode(StatusCode.Mobile.INVALID_PARAMETER);
        } else {
            result.setStatusCode(StatusCode.Mobile.OK);
            AppToAppData appToAppData = new AppToAppData();
            appToAppData.setActivationCode(input);
            result.setResult(appToAppData);
        }

        return result;
    }

}
