package br.com.horizon.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.horizon.model.Index;
import br.com.horizon.repository.callback.CallbackWithResponse;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.callback.ResponseCallback;
import br.com.horizon.repository.resource.Resource;
import br.com.horizon.retrofit.IndexRetrofit;
import br.com.horizon.retrofit.service.IndexService;
import retrofit2.Call;

public class IndexRepository {
    private final IndexService indexService;
    private final MutableLiveData<Resource<List<Index>>> selicLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Index>>> cdiLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Index>>> igpmLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Index>>> ipcaLiveData = new MutableLiveData<>();

    public IndexRepository() {
        this.indexService = new IndexRetrofit().getIndexService();
    }

    private void fetchSelicYearly(LoadedDataCallback<List<Index>> callback) {
        Call<List<Index>> call = indexService.getSelic();
        call.enqueue(new CallbackWithResponse<>(new ResponseCallback<List<Index>>() {
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }

            @Override
            public void onSuccess(List<Index> selic) {
                callback.onSuccess(selic);
            }
        }));
    }

    private void fetchIPCAYearly(LoadedDataCallback<List<Index>> callback) {
        Call<List<Index>> call = indexService.getIpca();
        call.enqueue(new CallbackWithResponse<>(new ResponseCallback<List<Index>>() {
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }

            @Override
            public void onSuccess(List<Index> ipcas) {
                callback.onSuccess(ipcas);
            }
        }));
    }

    private void fetchIGPMYearly(LoadedDataCallback<List<Index>> callback) {
        Call<List<Index>> call = indexService.getIgpm();
        call.enqueue(new CallbackWithResponse<>(new ResponseCallback<List<Index>>() {
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }

            @Override
            public void onSuccess(List<Index> igpms) {
                callback.onSuccess(igpms);
            }
        }));
    }

    private void fetchCdiYearly(LoadedDataCallback<List<Index>> callback) {
        Call<List<Index>> call = indexService.getCdi();
        call.enqueue(new CallbackWithResponse<>(new ResponseCallback<List<Index>>() {
            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }

            @Override
            public void onSuccess(List<Index> cdis) {
                callback.onSuccess(cdis);
            }
        }));
    }

    public LiveData<Resource<List<Index>>> getSELICLiveData() {
        fetchSelicYearly(new LoadedDataCallback<List<Index>>() {
            @Override
            public void onSuccess(List<Index> result) {
                selicLiveData.postValue(new Resource<>(result, null));
            }

            @Override
            public void onFail(String error) {
                selicLiveData.postValue(new Resource<>(null, error));
            }
        });

        return selicLiveData;
    }

    public LiveData<Resource<List<Index>>> getCdiLiveData() {
        fetchCdiYearly(new LoadedDataCallback<List<Index>>() {
            @Override
            public void onSuccess(List<Index> result) {
                cdiLiveData.postValue(new Resource<>(result, null));
            }

            @Override
            public void onFail(String error) {
                cdiLiveData.postValue(new Resource<>(null, error));
            }
        });
        return cdiLiveData;
    }

    public LiveData<Resource<List<Index>>> getIGPMLiveData() {
        fetchIGPMYearly(new LoadedDataCallback<List<Index>>() {
            @Override
            public void onSuccess(List<Index> result) {
                igpmLiveData.postValue(new Resource<>(result, null));
            }

            @Override
            public void onFail(String error) {
                igpmLiveData.postValue(new Resource<>(null, error));
            }
        });

        return igpmLiveData;
    }

    public LiveData<Resource<List<Index>>> getIPCALiveData() {
        fetchIPCAYearly(new LoadedDataCallback<List<Index>>() {
            @Override
            public void onSuccess(List<Index> result) {
                ipcaLiveData.postValue(new Resource<>(result, null));
            }

            @Override
            public void onFail(String error) {
                ipcaLiveData.postValue(new Resource<>(null, error));
            }
        });

        return ipcaLiveData;
    }

}
