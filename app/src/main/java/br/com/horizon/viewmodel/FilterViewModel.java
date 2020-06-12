package br.com.horizon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.Query;

import java.util.List;

import br.com.horizon.repository.SecurityRepository;


public class FilterViewModel extends ViewModel {
    private SecurityRepository securityRepository = new SecurityRepository();

    public LiveData<List<String>> getAllEmitters() {
        return securityRepository.getAllEmitters();
    }

    public LiveData<List<String>> getAllPublishers() {
        return securityRepository.getAllPublishers();
    }

    public LiveData<Double> getMinTax() {
        return securityRepository.getMinOrMaxInterest(Query.Direction.DESCENDING);
    }

    public LiveData<Double> getMaxTax() {
        return securityRepository.getMinOrMaxInterest(Query.Direction.ASCENDING);
    }
}
