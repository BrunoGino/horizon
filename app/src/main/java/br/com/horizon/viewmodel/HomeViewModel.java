package br.com.horizon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.horizon.model.Index;
import br.com.horizon.repository.IndexRepository;
import br.com.horizon.repository.resource.Resource;

public class HomeViewModel extends ViewModel {
    private final IndexRepository indexRepository;

    public HomeViewModel() {
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
