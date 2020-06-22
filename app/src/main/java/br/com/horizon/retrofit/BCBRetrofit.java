package br.com.horizon.retrofit;

import br.com.horizon.retrofit.service.BCBService;
import br.com.horizon.retrofit.service.IBGEService;
import lombok.Getter;
import retrofit2.Retrofit;

public class BCBRetrofit {
    private static final String BASE_URL = "https://api.bcb.gov.br/dados/serie/";
    @Getter
    private final BCBService bcbService;

    public BCBRetrofit() {
        RetrofitFactory retrofitFactory = new RetrofitFactory();
        Retrofit retrofit = retrofitFactory.getNewInstanceForURL(BASE_URL);
        bcbService = retrofit.create(BCBService.class);
    }
}
