package br.com.horizon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.horizon.model.Index;
import br.com.horizon.repository.IndexRepository;
import br.com.horizon.repository.resource.Resource;

public class IndexesViewModel extends ViewModel {
    private final IndexRepository indexRepository;

    public IndexesViewModel() {
        indexRepository = new IndexRepository();
    }

    public LiveData<Resource<List<Index>>> getSelicLiveData() {
        return indexRepository.getSELICLiveData();
    }

    public LiveData<Resource<List<Index>>> getCDILiveData() {
        return indexRepository.getCdiLiveData();
    }

    public LiveData<Resource<List<Index>>> getIPCALiveData() {
        return indexRepository.getIPCALiveData();
    }

    public LiveData<Resource<List<Index>>> getIGPMLiveData() {
        return indexRepository.getIGPMLiveData();
    }
}
