package br.com.horizon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.Query;

import java.util.Set;

import br.com.horizon.repository.SecurityRepository;
import br.com.horizon.repository.callback.LoadedDataCallback;


public class FilterViewModel extends ViewModel {
    private SecurityRepository securityRepository = new SecurityRepository();
    private MutableLiveData<Set<String>> emitters = new MutableLiveData<>();
    private MutableLiveData<Set<String>> publishers = new MutableLiveData<>();
    private MutableLiveData<Double> maxInterest = new MutableLiveData<>();
    private MutableLiveData<Double> minInterest = new MutableLiveData<>();

    private void getAllEmitters() {
        securityRepository.getAllEmitters(new LoadedDataCallback<Set<String>>() {
            @Override
            public void onSuccess(Set<String> result) {
                emitters.setValue(result);
            }

            @Override
            public void onFail(String error) {
            }
        });
    }

    private void getAllPublishers() {
        securityRepository.getAllPublishers(new LoadedDataCallback<Set<String>>() {
            @Override
            public void onSuccess(Set<String> result) {
                publishers.setValue(result);
            }

            @Override
            public void onFail(String error) {
            }
        });
    }

    private void fetchMaxInterest() {
        securityRepository.getMaxOrMinInterest(Query.Direction.DESCENDING, new LoadedDataCallback<Double>() {
            @Override
            public void onSuccess(Double result) {
                maxInterest.setValue(result);
            }

            @Override
            public void onFail(String error) {
            }
        });
    }

    private void fetchMinInterest() {
        securityRepository.getMaxOrMinInterest(Query.Direction.ASCENDING, new LoadedDataCallback<Double>() {
            @Override
            public void onSuccess(Double result) {
                minInterest.setValue(result);
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    public LiveData<Double> getMaxInterest() {
        fetchMaxInterest();
        return maxInterest;
    }

    public LiveData<Double> getMinInterest() {
        fetchMinInterest();
        return minInterest;
    }

    public LiveData<Set<String>> getEmitters() {
        getAllEmitters();
        return emitters;
    }

    public LiveData<Set<String>> getPublishers() {
        getAllPublishers();
        return publishers;
    }
}
