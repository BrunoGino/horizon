package br.com.horizon.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import br.com.horizon.model.Security;
import br.com.horizon.repository.SecurityRepository;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.resource.Resource;

public class SecurityDetailsViewModel extends ViewModel {

    private SecurityRepository repository;
    private MutableLiveData<Resource<Security>> liveDataById;

    public SecurityDetailsViewModel() {
        this.repository = new SecurityRepository();
        this.liveDataById = new MutableLiveData<>();

    }

    private void getResultById(String id) {
        repository.fetchById(id, new LoadedDataCallback<Security>() {
            @Override
            public void onSuccess(Security result) {
                liveDataById.setValue(new Resource<>(result, null));
                Log.d("ON-SUCCESS", liveDataById.getValue().getData().toString());
            }

            @Override
            public void onFail(String error) {
                liveDataById.setValue(new Resource<>(null, error));
                Log.d("ON-FAIL", liveDataById.getValue().getError());
            }
        });
    }

    public LiveData<Resource<Security>> getLiveDataById(String id) {
        getResultById(id);
        return liveDataById;
    }
}

