package it.bancomatpay.sdkui.utilities;

import android.app.Activity;

public interface GoToHomeInterface {
    void goToHome(Activity activity, boolean finishSdkFlow, boolean pinBlocked, boolean returnToIntro);
}
