package br.com.horizon.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppStateViewModel extends ViewModel {

    private MutableLiveData<VisualComponents> components;
    private VisualComponents hasComponents;

    public AppStateViewModel() {
        this.hasComponents = new VisualComponents();
        this.components = new MutableLiveData<>();
        components.setValue(hasComponents);
    }

    public void setComponents(VisualComponents components) {
        this.components.setValue(components);
    }

    public LiveData<VisualComponents> getComponents() {
        return components;
    }
}

