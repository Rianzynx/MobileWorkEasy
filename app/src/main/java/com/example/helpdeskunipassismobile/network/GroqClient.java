package com.example.helpdeskunipassismobile.network;

import android.util.Log;

import com.example.helpdeskunipassismobile.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroqClient {
    private static final String BASE_URL = "https://api.groq.com/openai/v1/";
    private static GroqService groqService;

    public static GroqService getGroqService() {
        if (groqService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            Log.d("GroqDebug", "API Key usada: " + BuildConfig.GROQ_API_KEY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + BuildConfig.GROQ_API_KEY)
                                .addHeader("Content-Type", "application/json")
                                .build();
                        return chain.proceed(newRequest);
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            groqService = retrofit.create(GroqService.class);
        }
        return groqService;
    }
}
