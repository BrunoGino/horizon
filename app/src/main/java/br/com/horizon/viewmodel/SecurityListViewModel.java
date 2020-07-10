package br.com.horizon.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.horizon.model.Security;
import br.com.horizon.repository.SecurityRepository;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.resource.Resource;

public class SecurityListViewModel extends ViewModel {

    private SecurityRepository securityRepository;
    private final MutableLiveData<Resource<List<Security>>> filteredLiveData;
    private final MutableLiveData<Resource<List<String>>> publishersLiveData;

    public SecurityListViewModel() {
        this.securityRepository = new SecurityRepository();
        this.filteredLiveData = new MutableLiveData<>();
        this.publishersLiveData = new MutableLiveData<>();
    }

    public LiveData<Resource<List<Security>>> fetchAll() {
        securityRepository.getAllSecurities(new LoadedDataCallback<List<Security>>() {
            @Override
            public void onSuccess(List<Security> result) {

                filteredLiveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onFail(String error) {
                filteredLiveData.setValue(new Resource<>(new ArrayList<>(), error));
            }
        });
        return filteredLiveData;
    }

    public LiveData<Resource<List<Security>>> fetchFilteredByType(String titleType) {
        securityRepository.getAllFilteredByType(titleType, new LoadedDataCallback<List<Security>>() {
            @Override
            public void onSuccess(List<Security> result) {
                filteredLiveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onFail(String error) {
                filteredLiveData.setValue(new Resource<>(new ArrayList<>(), error));
            }
        });
        return filteredLiveData;
    }

    public LiveData<Resource<List<Security>>> fetchFiltered(List<String> selectedIrs, List<String> selectedPublishers, String orderBy, String titleType) {
        securityRepository.getSecuritiesFiltered(selectedIrs, selectedPublishers, orderBy, titleType, new LoadedDataCallback<List<Security>>() {
            @Override
            public void onSuccess(List<Security> result) {
                Resource<List<Security>> resource = new Resource<>(result, null);
                Log.d("QUERYfirebaseVIEWMODEL", "onSuccess - View Model: " + resource.getData());
                filteredLiveData.setValue(resource);
            }

            @Override
            public void onFail(String error) {
                Resource<List<Security>> resource = new Resource<>(new ArrayList<>(), error);
                filteredLiveData.setValue(resource);
                Log.d("QUERYfirebaseVIEWMODEL", "onFail - View Model: " + resource.toString());
            }
        });
        return filteredLiveData;
    }

    public LiveData<Resource<List<String>>> fetchPublishersByType(String titleType) {
        securityRepository.getAllPublishersBySecurityType(titleType, new LoadedDataCallback<Set<String>>() {
            @Override
            public void onSuccess(Set<String> result) {
                publishersLiveData.setValue(new Resource<>(new ArrayList<>(result), null));
            }

            @Override
            public void onFail(String error) {
                publishersLiveData.setValue(new Resource<>(new ArrayList<>(), error));
            }
        });
        return publishersLiveData;
    }

}