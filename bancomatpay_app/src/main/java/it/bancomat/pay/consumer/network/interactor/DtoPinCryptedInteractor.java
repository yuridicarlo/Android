package it.bancomat.pay.consumer.network.interactor;

import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.Callable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.network.dto.request.DtoPinCrypted;
import it.bancomat.pay.consumer.network.totp.DerivedKeyInfoContainer;
import it.bancomat.pay.consumer.network.totp.PSKCManager;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.manager.task.ErrorException;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class DtoPinCryptedInteractor<E> implements Callable<DtoPinCrypted> {

    private final AppAuthenticationInterface authenticationInterface;
    private final E dtoRequest;

    private static final String TAG = DtoPinCryptedInteractor.class.getSimpleName();

    public DtoPinCryptedInteractor(AppAuthenticationInterface authenticationInterface, E dtoRequest) {
        this.authenticationInterface = authenticationInterface;
        this.dtoRequest = dtoRequest;
    }

    @Override
    public DtoPinCrypted call() throws Exception {
        Gson gson = new Gson();
        String jsonObject = gson.toJson(dtoRequest);
        CustomLogger.d(TAG, "plainMessage: " + jsonObject);
        CustomLogger.d(TAG, "*************************pskcEncryptDto start*************************");
        DtoPinCrypted dtoPinCrypted = new DtoPinCrypted();
        try {
            CustomLogger.d(TAG, "*************************getSeed start*************************");
            byte[] seed = authenticationInterface.getSeed();
            CustomLogger.d(TAG, "*************************getSeed end*************************");
            CustomLogger.d(TAG, "*************************getHmacKey start*************************");
            byte[] hmacKeyRaw = authenticationInterface.getHmacKey();
            CustomLogger.d(TAG, "*************************getHmacKey end*************************");
            dtoPinCrypted = pskcEncryptDto(jsonObject, seed, hmacKeyRaw);
        } catch (Exception e) {
            CustomLogger.e("PinCryptedTask", "ex" + e.getMessage(), e);
            throw new ErrorException(new Error(e));
        }

        return dtoPinCrypted;
    }

    private DtoPinCrypted pskcEncryptDto(String payload, byte[] seed, byte[] hmacKeyRaw) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidAlgorithmParameterException, IOException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {

        SecretKeySpec hmacKey = new SecretKeySpec(hmacKeyRaw, "AES");

        CustomLogger.d(TAG, "seed:" + new String(Base64.encode(seed, Base64.DEFAULT)));
        String userInfo = AppBancomatDataManager.getInstance().getUserAccountId();

        DtoPinCrypted dtoPinCrypted = new DtoPinCrypted();
        CustomLogger.d(TAG, "+++++++++++getTOTPDerivedKeyFromSeed start+++++++++++");
        DerivedKeyInfoContainer totpDerivedKeyInfo = PSKCManager.getInstance().getTOTPDerivedKeyFromSeed(seed);
        CustomLogger.d(TAG, "+++++++++++getTOTPDerivedKeyFromSeed start+++++++++++");
        String strSalt = totpDerivedKeyInfo.getSaltB64();
        int counter = totpDerivedKeyInfo.getItCounter();
        CustomLogger.d(TAG, "+++++++++++encPayload start+++++++++++");
        String encPayload = PSKCManager.getInstance().encryptString(payload, totpDerivedKeyInfo.getKey(), hmacKey);
        CustomLogger.d(TAG, "+++++++++++encPayload end+++++++++++");
        CustomLogger.d(TAG, "+++++++++++encUserInfo start+++++++++++");
        String encUserInfo = PSKCManager.getInstance().encryptString(userInfo, totpDerivedKeyInfo.getKey(), hmacKey);
        CustomLogger.d(TAG, "+++++++++++encUserInfo end+++++++++++");
        dtoPinCrypted.setIteratorCounter(counter);
        dtoPinCrypted.setSalt(strSalt);
        dtoPinCrypted.setPayload(encPayload);
        dtoPinCrypted.setCryptInfo(encUserInfo);
        CustomLogger.d(TAG, "*************************pskcEncryptDto end*************************");
        return dtoPinCrypted;
    }

}