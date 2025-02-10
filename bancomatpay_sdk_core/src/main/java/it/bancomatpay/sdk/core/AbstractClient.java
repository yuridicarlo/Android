package it.bancomatpay.sdk.core;


import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import it.bancomatpay.sdk.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;

public abstract class AbstractClient {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    public static final Executor THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(CORE_POOL_SIZE);

    public abstract int getTimeOut();

    public abstract String baseUrl();

    public abstract List<Interceptor> getInterceptors();

    public abstract Converter.Factory getConverterFactory();

    public Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .client(getClient())
                .baseUrl(baseUrl())
                .callbackExecutor(THREAD_POOL_EXECUTOR)
                .addConverterFactory(getConverterFactory())
                .build();
    }

    public OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(getTimeOut(), TimeUnit.SECONDS)
                .writeTimeout(getTimeOut(), TimeUnit.SECONDS)
                .readTimeout(getTimeOut(), TimeUnit.SECONDS);

        if (getInterceptors() != null) {
            for (Interceptor interceptor : getInterceptors()) {
                builder.addInterceptor(interceptor);
            }
        }
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }
}

