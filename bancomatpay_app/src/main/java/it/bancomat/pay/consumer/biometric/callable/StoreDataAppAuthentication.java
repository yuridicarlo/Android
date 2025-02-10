package it.bancomat.pay.consumer.biometric.callable;

import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.concurrent.Callable;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import it.bancomat.pay.consumer.biometric.SecurityConstants;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.Conversion;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;


public class StoreDataAppAuthentication extends CallableVoid {

    private final byte[] data;
    private final static String TAG = StoreDataAppAuthentication.class.getSimpleName();

    public StoreDataAppAuthentication(byte[] data) {
        this.data = data;
    }

    @Override
    public void execute() throws Exception {
        PublicKey publicBiometricKey = new GetPublicKey().call();

        //Asymmetric encryption of AES KEY
        Cipher cipherBiometricRSA = Cipher.getInstance(SecurityConstants.BIO_TRANSFORMATION_RSA);
        AlgorithmParameterSpec algorithmParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
        cipherBiometricRSA.init(Cipher.ENCRYPT_MODE, publicBiometricKey, algorithmParameterSpec);

        KeyGenerator keyGenerator = KeyGenerator.getInstance(SecurityConstants.TYPE_AES);
        keyGenerator.init(128);

        SecretKey aesKey = keyGenerator.generateKey();
        byte[] cipherBiometricKey = cipherBiometricRSA.doFinal(aesKey.getEncoded());

        AppBancomatDataManager dataManager = AppBancomatDataManager.getInstance();

        dataManager.putBiometricAESKey(cipherBiometricKey);

        Cipher cipher = Cipher.getInstance(SecurityConstants.CHIPER_AES_CBC_PKCS7PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        dataManager.putAESIV(cipher.getIV());
        byte[] newPaymentTokenCipher = cipher.doFinal(data);
        dataManager.putDataAppAuthentication(newPaymentTokenCipher);
        CustomLogger.d(TAG, "plain " + Conversion.byteArrayToStringBase64(data));
    }
}
