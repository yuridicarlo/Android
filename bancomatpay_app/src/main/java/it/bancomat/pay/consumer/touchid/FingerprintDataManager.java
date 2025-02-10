package it.bancomat.pay.consumer.touchid;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Base64;

import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import it.bancomat.pay.consumer.BancomatApplication;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public final class FingerprintDataManager {

    private static FingerprintDataManager INSTANCE;

    private static final String TAG = FingerprintDataManager.class.getSimpleName();

    private static final String ALIAS = "alias";

    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private KeyguardManager keyguardManager;

    private FingerprintManagerCompat fingerprintManager;

    @TargetApi(Build.VERSION_CODES.M)
    private FingerprintDataManager(Context context) {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyguardManager = context.getSystemService(KeyguardManager.class);
                fingerprintManager = FingerprintManagerCompat.from(context);
            }
        } catch (Exception e) {
            CustomLogger.e(TAG, "Error when init FingerprintDataManager", e);
        }
    }

    public synchronized static FingerprintDataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FingerprintDataManager(BancomatApplication.getAppContext());
        }
        return INSTANCE;
    }

    synchronized void store(Cipher cipher, byte[] input) {

        try {
            if (isFingerprintAvailable()) {
                CustomLogger.d(TAG, "String input: " + Base64.encodeToString(input, Base64.DEFAULT));

                byte[] encryptValue = encrypt(cipher, input);

                CustomLogger.d(TAG, "String encrypted: " + Base64.encodeToString(encryptValue, Base64.DEFAULT));

                AppBancomatDataManager.getInstance().putTIdData(encryptValue);
            }
        } catch (Exception e) {
            delete();
        }
    }

    private byte[] decrypt(Cipher cipher, byte[] raw) {
        try {
            return cipher.doFinal(raw);
        } catch (IllegalBlockSizeException e) {
            CustomLogger.e(TAG, "Error in encrypt", e);
            FingerprintDataManager.getInstance().delete();
        } catch (BadPaddingException e) {
            CustomLogger.e(TAG, "Error in encrypt", e);

        }
        return null;
    }

    private byte[] encrypt(Cipher cipher, byte[] clear) {
        try {
            byte[] encryptedData = cipher.doFinal(clear);
            IvParameterSpec ivParams = cipher.getParameters().getParameterSpec(IvParameterSpec.class);
            byte[] iv = ivParams.getIV();
            AppBancomatDataManager.getInstance().putIvData(iv);
            return encryptedData;
        } catch (Exception e) {
            CustomLogger.e(TAG, "Error in encrypt", e);
            return null;
        }

    }

    synchronized byte[] read(Cipher cipher) {

        try {

            byte[] decrypted = decrypt(cipher, AppBancomatDataManager.getInstance().getTIdData());
            CustomLogger.d(TAG, "String decrypted: " + Base64.encodeToString(decrypted, Base64.DEFAULT));

            return decrypted;
        } catch (Exception e) {
            CustomLogger.e(TAG, "Error in decrypted", e);
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    Cipher cipherInit(int typeOperation) {
        try {

            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyStore.load(null);

            SecretKey key = (SecretKey) keyStore.getKey(ALIAS, null);
            if (typeOperation == Cipher.ENCRYPT_MODE) {
                cipher.init(typeOperation, key);
            } else {

                byte[] iv = AppBancomatDataManager.getInstance().getIvData();
                IvParameterSpec ivParams = new IvParameterSpec(iv);
                cipher.init(typeOperation, key, ivParams);
            }
            return cipher;
        } catch (Exception e) {
            CustomLogger.e(TAG, "Failed to get Cipher", e);
            return null;
        }
    }

    void generateKey() {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                keyStore.load(null);
                // Set the alias of the entry in Android KeyStore where the key will appear
                // and the constrains (purposes) in the constructor of the Builder

                KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        // Require the user to authenticate with a fingerprint to authorize every use
                        // of the key
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

                keyGenerator.init(builder.build());
                keyGenerator.generateKey();

            }
        } catch (Exception e) {
            CustomLogger.e(TAG, "Failed to get KeyGenerator instance", e);

        }
    }

    private boolean existsKey() {
        try {
            initKeyStore();
            return keyStore.containsAlias(ALIAS) && (AppBancomatDataManager.getInstance().getTIdData() != null);
        } catch (Exception e) {
            return false;
        }
    }

    private void initKeyStore() {
        try {
            keyStore.load(null);
        } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
            CustomLogger.e(TAG, "Failed to init keystore", e);
        }
    }

    public void delete() {
        if (keyStore != null) {
            try {
                initKeyStore();
                keyStore.deleteEntry(ALIAS);
                AppBancomatDataManager.getInstance().deleteTouchIdData();

            } catch (Exception ignored) {

            }
        }
    }

    private boolean isSoftwareRequisitesAvailable() {
	    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private boolean isKeyguardSetUp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        return (keyguardManager != null && keyguardManager.isKeyguardSecure());
    }

    private boolean isSensorAvailable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(BancomatApplication.getAppContext(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
                && fingerprintManager != null) {
            return fingerprintManager.isHardwareDetected();
        }
        return false;
    }

    private boolean isFingerprintAvailable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(BancomatApplication.getAppContext(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
                && fingerprintManager != null) {
            return isSensorAvailable() && fingerprintManager.hasEnrolledFingerprints();
        }
        return false;
    }

    public boolean isFingerprintEnrolled() {
        return isFingerprintAvailable() && existsKey() && AppBancomatDataManager.getInstance().getTIdData() != null;
    }

    public FingerprintState getFingerprintState() {
        if (!isSoftwareRequisitesAvailable()) {
            return FingerprintState.MISSING_SOFTWARE_REQUISITES;
        }
        if (!isSensorAvailable()) {
            return FingerprintState.MISSING_FINGERPRINT_SENSOR;
        }
        if (!isFingerprintAvailable()) {
            return FingerprintState.FINGERPRINT_NOT_ENABLED_ON_DEVICE;
        }
        if (!isKeyguardSetUp()) {
            return FingerprintState.MISSING_KEYGUARD_MANAGER;
        }
        if (!isFingerprintEnrolled()) {
            return FingerprintState.DISABLED;
        }
        return FingerprintState.ENABLED;
    }

}
