package br.com.horizon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import br.com.horizon.model.Security;
import br.com.horizon.repository.SecurityRepository;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.resource.Resource;

public class SecurityListViewModel extends ViewModel {

    private SecurityRepository securityRepository;
    private final MutableLiveData<Resource<List<Security>>> filteredLiveData;

    public SecurityListViewModel() {
        this.securityRepository = new SecurityRepository();
        this.filteredLiveData = new MutableLiveData<>();
    }

    public LiveData<Resource<List<Security>>> fetchAll() {
        return securityRepository.fetchAll();
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

    public LiveData<Resource<List<Security>>> fetchFirstHundredWithGreatestInterest() {
        securityRepository.getFirstHundredWithGreatestInterest(new LoadedDataCallback<List<Security>>() {
            @Override
            public void onSuccess(List<Security> result) {
                filteredLiveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onFail(String error) {
                filteredLiveData.setValue(new Resource<>(null, error));
            }
        });
        return filteredLiveData;
    }

    public LiveData<Resource<List<Security>>> fetchMostFavorited() {
        securityRepository.getMostFavorited(new LoadedDataCallback<List<Security>>() {
            @Override
            public void onSuccess(List<Security> result) {

            }

            @Override
            public void onFail(String error) {

            }
        });
        return filteredLiveData;
    }
}