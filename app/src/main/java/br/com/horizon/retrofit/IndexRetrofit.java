package br.com.horizon.retrofit;

import br.com.horizon.retrofit.service.IndexService;
import lombok.Getter;
import retrofit2.Retrofit;

public class IndexRetrofit {
    private static final String BASE_URL = "https://indexes-br-backup.herokuapp.com";
    @Getter
    private final IndexService indexService;

    public IndexRetrofit() {
        RetrofitFactory retrofitFactory = new RetrofitFactory();
        Retrofit retrofit = retrofitFactory.getNewInstanceForURL(BASE_URL);
        indexService = retrofit.create(IndexService.class);
    }
}
