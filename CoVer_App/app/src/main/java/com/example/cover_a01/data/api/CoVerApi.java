package com.example.cover_a01.data.api;

import com.example.cover_a01.data.model.Exposee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CoVerApi {
    String BASE_URL = "https://api.cover.<domain>/";

    @GET("/exposees")
    Call<List<Exposee>> exposeesGet();

    @POST("/reportInfection")
    Call<Void> reportInfectionPost(@Query("validationCode")String validationCode, @Query("keys")List<String> keys);
}
