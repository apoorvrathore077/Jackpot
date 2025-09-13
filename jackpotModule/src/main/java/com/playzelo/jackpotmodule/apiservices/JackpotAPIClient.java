package com.playzelo.jackpotmodule.apiservices;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JackpotAPIClient {
    private static final String BASE_URL = "https://jackpot-game.onrender.com/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging Interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Retry Interceptor
            Interceptor retryInterceptor = new Interceptor() {

                @NonNull
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    IOException exception = null;
                    Response response = null;

                    int maxRetry = 3;
                    for (int attempt = 0; attempt < maxRetry; attempt++) {
                        try {
                            response = chain.proceed(request);
                            if (response.isSuccessful()) {
                                return response;
                            }
                        } catch (IOException e) {
                            exception = e;
                        }
                    }
                    // Agar sab retry fail ho gaye to exception throw karo
                    if (exception != null) throw exception;
                    return response;
                }
            };

            // OkHttp Client
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(retryInterceptor)
                    .build();

            // Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
