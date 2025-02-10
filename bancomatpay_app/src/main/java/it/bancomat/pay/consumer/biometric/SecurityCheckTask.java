package it.bancomat.pay.consumer.biometric;

import android.app.KeyguardManager;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.biometric.exception.DeviceNotSecuredException;
import it.bancomatpay.sdk.core.PayCore;


public class SecurityCheckTask implements Callable<Void> {

    private final SecurityCheckMode checkMode;

    public SecurityCheckTask(@NonNull SecurityCheckMode checkMode) {
        this.checkMode = checkMode;
    }

    private void checkDeviceSecured(Context context) throws Exception {
        KeyguardManager manager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (!manager.isDeviceSecure()) {
            throw new DeviceNotSecuredException("Secure lock screen must be enabled to create keys requiring user authentication");
        }
    }

    @Override
    public Void call() throws Exception {
        Context context = PayCore.getAppContext();

        switch (checkMode) {
            case ROOT_ONLY:

                //TODO

                break;
            case DEVICE_SECURED_ONLY:
                checkDeviceSecured(context);
                break;
            case FULL_CHECK:

                //TODO

                break;
            default:
                break;
        }
        return null;
    }
}
