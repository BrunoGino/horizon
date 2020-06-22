package br.com.horizon.retrofit.service;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BCBService {
    @GET("bcdata.sgs.1178/dados/ultimos/1?formato=json")
    Call<JsonArray> getSELICYearly();

    @GET("bcdata.sgs.4175/dados/ultimos/1?formato=json")
    Call<JsonArray> getIGPMYearly();
}
