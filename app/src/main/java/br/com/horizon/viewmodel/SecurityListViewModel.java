package br.com.horizon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.horizon.model.Filter;
import br.com.horizon.model.Security;
import br.com.horizon.repository.SecurityRepository;
import br.com.horizon.repository.resource.Resource;

public class SecurityListViewModel extends ViewModel {

    private SecurityRepository securityRepository;

    public SecurityListViewModel() {
        this.securityRepository = new SecurityRepository();
    }

    public LiveData<Resource<List<Security>>> fetchAll() {
        return securityRepository.fetchAll();
    }

    public LiveData<Resource<List<Security>>> fetchFiltered(Filter filter) {
        return securityRepository.fetchFiltered(filter);
    }
}