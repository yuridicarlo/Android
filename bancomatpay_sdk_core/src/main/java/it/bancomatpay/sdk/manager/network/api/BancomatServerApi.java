package it.bancomatpay.sdk.manager.network.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.bancomatpay.sdk.BuildConfig;
import it.bancomatpay.sdk.core.AbstractClient;
import it.bancomatpay.sdk.manager.network.ClientBuilderFactory;
import it.bancomatpay.sdk.manager.utilities.Constants;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

import static it.bancomatpay.sdk.manager.utilities.Constants.TIMEOUT;


public class BancomatServerApi extends AbstractClient {

    private static BancomatServer loyaltyServer;

    public static BancomatServer getBancomatServer() {
        if (loyaltyServer == null)
            loyaltyServer = new BancomatServerApi().getRetrofit().create(BancomatServer.class);

        return loyaltyServer;
    }

    @Override
    public int getTimeOut() {
        return TIMEOUT;
    }

    @Override
    public String baseUrl() {
        return Constants.BASE_URL;
    }

    @Override
    public List<Interceptor> getInterceptors() {
        return new ArrayList<>();
    }

    @Override
    public Converter.Factory getConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Override
    public OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(getTimeOut(), TimeUnit.SECONDS)
                .writeTimeout(getTimeOut(), TimeUnit.SECONDS)
                .readTimeout(getTimeOut(), TimeUnit.SECONDS);

        builder = ClientBuilderFactory.getBuilder(builder);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }
}

