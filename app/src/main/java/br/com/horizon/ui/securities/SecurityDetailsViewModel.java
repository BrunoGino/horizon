package br.com.horizon.ui.securities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import br.com.horizon.model.Security;
import br.com.horizon.repository.SecurityRepository;

public class SecurityDetailsViewModel extends ViewModel {

    private SecurityRepository repository;

    public SecurityDetailsViewModel() {
        this.repository = new SecurityRepository();

    }

    public LiveData<Security> fetchById(String id) {
        return repository.fetchById(id);
    }

}

