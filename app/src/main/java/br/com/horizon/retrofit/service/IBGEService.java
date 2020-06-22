package br.com.horizon.retrofit.service;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IBGEService {
    @GET("v3/agregados/1737/periodos/202005/variaveis/2265?localidades=N1[all]")
    Call<JsonArray> getIPCAYearly();
}
