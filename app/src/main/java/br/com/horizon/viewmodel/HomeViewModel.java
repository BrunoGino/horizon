package br.com.horizon.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import br.com.horizon.repository.BCBRepository;
import br.com.horizon.repository.IBGERepository;
import br.com.horizon.repository.resource.Resource;

public class HomeViewModel extends ViewModel {
    private final MediatorLiveData<Float[]> mediatorLiveData;
    private final BCBRepository bcbRepository;
    private final IBGERepository ibgeRepository;

    public HomeViewModel() {
        bcbRepository = new BCBRepository();
        ibgeRepository = new IBGERepository();
        mediatorLiveData = new MediatorLiveData<>();
    }

    public void observeIndexes(LifecycleOwner lifecycleOwner, Observer<Float[]> observer) {
        Float[] indexes = new Float[4];

        mediatorLiveData.observe(lifecycleOwner, observer);

        mediatorLiveData.addSource(bcbRepository.getSELICLiveData(), floatResource -> {
            if (floatResource.getData() != null) {
                indexes[0] = floatResource.getData();
                mediatorLiveData.postValue(indexes);
            }
        });
        mediatorLiveData.addSource(bcbRepository.getCdiLiveData(), floatResource -> {
            if (floatResource.getData() != null) {
                indexes[1] = floatResource.getData();
                mediatorLiveData.postValue(indexes);
            }
        });
        mediatorLiveData.addSource(ibgeRepository.getIPCALiveData(), floatResource -> {
            if (floatResource.getData() != null) {
                indexes[2] = floatResource.getData();
                mediatorLiveData.postValue(indexes);
            }
        });
        mediatorLiveData.addSource(bcbRepository.getIGPMLiveData(), floatResource -> {
            if (floatResource.getData() != null) {
                indexes[3] = floatResource.getData();
                mediatorLiveData.postValue(indexes);
            }
        });
    }

}
