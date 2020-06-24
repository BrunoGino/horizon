package br.com.horizon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.horizon.model.Security;
import br.com.horizon.repository.SecurityRepository;

public class SecurityFavoritesViewModel extends ViewModel {
    private SecurityRepository securityRepository;

    public SecurityFavoritesViewModel() {
        securityRepository = new SecurityRepository();
    }

    public LiveData<List<Security>> getMyFavorites() {
        return null;
    }
}
