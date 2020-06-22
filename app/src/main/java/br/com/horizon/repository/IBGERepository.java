package br.com.horizon.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.com.horizon.repository.callback.CallbackWithResponse;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.callback.ResponseCallback;
import br.com.horizon.repository.resource.Resource;
import br.com.horizon.retrofit.IBGERetrofit;
import br.com.horizon.retrofit.service.IBGEService;
import retrofit2.Call;

public class IBGERepository {
    private final IBGEService ibgeService;
    private final MutableLiveData<Resource<Float>> ipcaLiveData = new MutableLiveData<>();


    public IBGERepository() {
        ibgeService = new IBGERetrofit().getIbgeService();
    }

    private void fetchIPCAYearly(LoadedDataCallback<JsonArray> callback) {
        Call<JsonArray> call = ibgeService.getIPCAYearly();

        call.enqueue(new CallbackWithResponse<>(new ResponseCallback<JsonArray>() {
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }

            @Override
            public void onSuccess(JsonArray jsonObject) {
                callback.onSuccess(jsonObject);
            }
        }));
    }

    public LiveData<Resource<Float>> getIPCALiveData() {
        fetchIPCAYearly(new LoadedDataCallback<JsonArray>() {
            @Override
            public void onSuccess(JsonArray result) {
                float indexValueFromJson = getIndexValueFromJson(result) / 100;
                ipcaLiveData.postValue(new Resource<>(indexValueFromJson, null));
            }

            @Override
            public void onFail(String error) {
                ipcaLiveData.setValue(new Resource<>(0.f, error));
            }
        });

        return ipcaLiveData;
    }

    private float getIndexValueFromJson(JsonArray result) {
        JsonObject rootObject = result.get(0).getAsJsonObject();
        JsonArray resultados = rootObject.getAsJsonArray("resultados");
        JsonObject resultadosChild = resultados.get(0).getAsJsonObject();
        JsonArray series = resultadosChild.getAsJsonArray("series");
        JsonObject seriesChild = series.get(0).getAsJsonObject();
        JsonObject serie = seriesChild.get("serie").getAsJsonObject();
        return serie.get("202005").getAsFloat();
    }

}
