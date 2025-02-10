package it.bancomat.pay.consumer.network.dto;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

import it.bancomat.pay.consumer.network.totp.PSKCManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class AuthenticationData implements AppAuthenticationInterface {

    private static String TAG = AuthenticationData.class.getSimpleName();

    private String pin;
    private byte[] seed;
    private byte[] hmacKey;

    public AuthenticationData(String pin) {
        this.pin = pin;
    }

    private SecretKey pinDerived;

    private SecretKey getPinDerivedKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (pinDerived == null) {
            pinDerived = PSKCManager.getInstance().getPinDerivedKeyFrom(pin);
        }
        return pinDerived;
    }

    @Override
    public byte[] getSeed() {
        if (seed == null) {
            try {
                CustomLogger.d(TAG, "*************************getPinDerivedKeyFrom pin start*************************");
                SecretKey pinDerived = getPinDerivedKey();
                CustomLogger.d(TAG, "*************************getPinDerivedKeyFrom pin end*************************");
                CustomLogger.d(TAG, "*************************getSeedWithPinDerivedKey start*************************");
                seed = PSKCManager.getInstance().getSeedWithPinDerivedKey(pinDerived);
                CustomLogger.d(TAG, "*************************getSeedWithPinDerivedKey end*************************");
            } catch (Exception e) {
                CustomLogger.e(TAG, e.getMessage(), e);
            }
        }
        return seed;
    }

    @Override
    public byte[] getHmacKey() {
        if (hmacKey == null) {
            try {
                CustomLogger.d(TAG, "*************************getPinDerivedKeyFrom pin start*************************");
                SecretKey pinDerived = getPinDerivedKey();
                CustomLogger.d(TAG, "*************************getPinDerivedKeyFrom pin end*************************");
                CustomLogger.d(TAG, "*************************getHmacKeyWithPinDerivedKey start*************************");
                hmacKey = PSKCManager.getInstance().getHmacKeyWithPinDerivedKey(pinDerived);
                CustomLogger.d(TAG, "*************************getHmacKeyWithPinDerivedKey end*************************");
            } catch (Exception e) {
                CustomLogger.e(TAG, e.getMessage(), e);
            }
        }
        return hmacKey;
    }

}
