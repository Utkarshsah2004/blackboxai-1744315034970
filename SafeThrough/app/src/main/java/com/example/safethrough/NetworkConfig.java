package com.example.safethrough;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class NetworkConfig {
    private static final String BASE_URL = "https://your-api-base-url.com/"; // Replace with your actual API base URL
    private static NetworkConfig instance;
    private Retrofit retrofit;

    private NetworkConfig() {
        // Create logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttpClient with timeout and logging
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        // Create Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized NetworkConfig getInstance() {
        if (instance == null) {
            instance = new NetworkConfig();
        }
        return instance;
    }

    public EmergencyApiService getEmergencyApiService() {
        return retrofit.create(EmergencyApiService.class);
    }

    // Method to update base URL if needed (e.g., for different environments)
    public void updateBaseUrl(String newBaseUrl) {
        retrofit = retrofit.newBuilder()
                .baseUrl(newBaseUrl)
                .build();
    }
}
