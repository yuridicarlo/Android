package it.bancomatpay.sdk.manager.network;

import com.guardsquare.dexguard.runtime.net.PublicKeyTrustManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import okhttp3.OkHttpClient;

public class ClientBuilderFactory {

    protected static String TAG = ClientBuilderFactory.class.getSimpleName();

    public static OkHttpClient.Builder getBuilder(OkHttpClient.Builder builder) {

        try {

            if (Constants.PINNING_ENABLED) {

                SSLContext sslContext = SSLContext.getInstance("SSL");

                CustomLogger.d(TAG, "Creating the Dexguard Trust Manager using the public keys...");
                PublicKeyTrustManager trustManager = new PublicKeyTrustManager(Constants.TRUSTED_CERTIFICATES);

                sslContext.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                builder.sslSocketFactory(sslSocketFactory, trustManager);
            }
        } catch (Exception e) {
            CustomLogger.d(TAG, "Something wrong");
        }

        return builder;
    }
}
