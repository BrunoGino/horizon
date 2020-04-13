package br.com.horizon.ui.securities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.horizon.model.Security;
import br.com.horizon.repository.SecurityRepository;
import br.com.horizon.repository.resource.Resource;

public class SecurityViewModel extends ViewModel {

    private SecurityRepository securityRepository;

    public SecurityViewModel() {
        this.securityRepository = new SecurityRepository();
    }

    public LiveData<Resource<List<Security>>> fetchAll() {
        return securityRepository.fetchAll();
    }

    public LiveData<Resource<Security>> fetchById(String id) {
        return securityRepository.fetchById(id);
    }


}