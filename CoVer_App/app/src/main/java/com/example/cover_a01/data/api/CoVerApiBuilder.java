package com.example.cover_a01.data.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoVerApiBuilder {

    static CoVerApi instance;

    public static CoVerApi getCoVerApi(){
        if(instance==null){
            // Define the format how the date should be parsed.
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CoVerApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            instance = retrofit.create(CoVerApi.class);
        }
        return instance;
    }
}
