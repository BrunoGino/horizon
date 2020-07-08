package br.com.horizon.retrofit.service;

import java.util.List;

import br.com.horizon.model.Index;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IndexService {
    @GET("/selic")
    Call<List<Index>> getSelic();
    @GET("/cdi")
    Call<List<Index>> getCdi();
    @GET("/ipca")
    Call<List<Index>> getIpca();
    @GET("/igpm")
    Call<List<Index>> getIgpm();
}
