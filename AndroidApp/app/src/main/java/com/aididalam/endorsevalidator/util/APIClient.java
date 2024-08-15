package com.aididalam.endorsevalidator.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit;
    private static Gson gson;

    public static Retrofit getRetrofit() {
        gson = new GsonBuilder()
                .create();

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.api)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
