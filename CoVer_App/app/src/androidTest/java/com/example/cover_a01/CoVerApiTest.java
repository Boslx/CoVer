package com.example.cover_a01;

import com.example.cover_a01.data.api.CoVerApi;
import com.example.cover_a01.data.api.CoVerApiBuilder;
import com.example.cover_a01.data.model.Exposee;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CoVerApiTest {
    private CoVerApi coVerApi;

    @Before
    public void setUp() throws Exception {
        coVerApi = CoVerApiBuilder.getCoVerApi();
    }

    @Test
    public void testExposeesGet() throws IOException {
        Call<List<Exposee>> call = coVerApi.exposeesGet();
        Response<List<Exposee>> response = call.execute();
        Assert.assertTrue(response.isSuccessful());
    }

    @Test
    public void testReportInfectionPost() throws IOException {
        Call<Void> call = coVerApi.reportInfectionPost("123", new ArrayList<String>());
        Response<Void> response = call.execute();
        Assert.assertEquals("\"Invalid Validationcode!\"", response.errorBody().string());
    }
}