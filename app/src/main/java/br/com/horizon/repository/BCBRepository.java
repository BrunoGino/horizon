package br.com.horizon.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Objects;

import br.com.horizon.repository.callback.CallbackWithResponse;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.callback.ResponseCallback;
import br.com.horizon.repository.resource.Resource;
import br.com.horizon.retrofit.BCBRetrofit;
import br.com.horizon.retrofit.service.BCBService;
import retrofit2.Call;

public class BCBRepository {
    private final BCBService bcbService;
    private final MutableLiveData<Resource<Float>> selicLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Float>> cdiLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Float>> igpmLiveData = new MutableLiveData<>();

    public BCBRepository() {
        this.bcbService = new BCBRetrofit().getBcbService();
    }

    private void fetchSELICYearly(LoadedDataCallback<JsonArray> callback) {
        Call<JsonArray> call = bcbService.getSELICYearly();
        call.enqueue(new CallbackWithResponse<>(new ResponseCallback<JsonArray>() {
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }

            @Override
            public void onSuccess(JsonArray selic) {
                callback.onSuccess(selic);
            }
        }));
    }

    private void fetchIGPMYearly(LoadedDataCallback<JsonArray> callback) {
        Call<JsonArray> call = bcbService.getIGPMYearly();
        call.enqueue(new CallbackWithResponse<>(new ResponseCallback<JsonArray>() {
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }

            @Override
            public void onSuccess(JsonArray igpm) {
                callback.onSuccess(igpm);
            }
        }));
    }

    public LiveData<Resource<Float>> getSELICLiveData() {
        fetchSELICYearly(new LoadedDataCallback<JsonArray>() {
            @Override
            public void onSuccess(JsonArray result) {
                float valueToPercent = (getValueFromJSON(result) + 0.1f) / 100;
                selicLiveData.postValue(new Resource<>(valueToPercent, null));
            }

            @Override
            public void onFail(String error) {
                selicLiveData.postValue(new Resource<>(0.f, error));
            }
        });

        return selicLiveData;
    }

    public LiveData<Resource<Float>> getCdiLiveData() {
        fetchSELICYearly(new LoadedDataCallback<JsonArray>() {
            @Override
            public void onSuccess(JsonArray result) {
                float valueToPercent = getValueFromJSON(result) / 100;
                cdiLiveData.postValue(new Resource<>(valueToPercent, null));
            }

            @Override
            public void onFail(String error) {
                cdiLiveData.postValue(new Resource<>(0.f, error));
            }
        });
        return cdiLiveData;
    }

    public LiveData<Resource<Float>> getIGPMLiveData() {
        fetchIGPMYearly(new LoadedDataCallback<JsonArray>() {
            @Override
            public void onSuccess(JsonArray result) {
                float valueToPercent = getValueFromJSON(result) / 100;
                igpmLiveData.postValue(new Resource<>(valueToPercent, null));
            }

            @Override
            public void onFail(String error) {
                igpmLiveData.postValue(new Resource<>(0.f, error));
            }
        });

        return igpmLiveData;
    }

    private float getValueFromJSON(JsonArray result) {
        JsonElement jsonElement = result.get(0);
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        return asJsonObject.get("valor").getAsFloat();
    }
}
