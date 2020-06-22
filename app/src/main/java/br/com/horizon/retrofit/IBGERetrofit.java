package br.com.horizon.retrofit;

import br.com.horizon.retrofit.service.IBGEService;
import lombok.Getter;
import retrofit2.Retrofit;

public class IBGERetrofit {
    private static final String BASE_URL = "https://servicodados.ibge.gov.br/api/";
    @Getter
    private final IBGEService ibgeService;

    public IBGERetrofit() {
        RetrofitFactory retrofitFactory = new RetrofitFactory();
        Retrofit retrofit = retrofitFactory.getNewInstanceForURL(BASE_URL);
        ibgeService = retrofit.create(IBGEService.class);
    }

}
