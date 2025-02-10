package it.bancomat.pay.consumer.network.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.bancomat.pay.consumer.network.AppClientBuilderFactory;
import it.bancomat.pay.consumer.utilities.AppConstants;
import it.bancomatpay.consumer.BuildConfig;
import it.bancomatpay.sdk.core.AbstractClient;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppBancomatServerApi extends AbstractClient {

    private static AppBancomatServer loyaltyServer;

    public static AppBancomatServer getBancomatServer() {
        if (loyaltyServer == null)
            loyaltyServer = new AppBancomatServerApi().getRetrofit().create(AppBancomatServer.class);

        return loyaltyServer;
    }

    @Override
    public int getTimeOut() {
        return AppConstants.TIMEOUT;
    }

    @Override
    public String baseUrl() {
        return AppConstants.BASE_URL;
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

        builder = AppClientBuilderFactory.getBuilder(builder);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }

}

