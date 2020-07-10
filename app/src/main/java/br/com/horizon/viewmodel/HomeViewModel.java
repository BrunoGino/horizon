package br.com.horizon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import br.com.horizon.model.User;
import br.com.horizon.repository.UserRepository;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.resource.Resource;

public class HomeViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Resource<User>> userMutableLiveData;

    public HomeViewModel() {
        userRepository = new UserRepository();
        userMutableLiveData = new MutableLiveData<>();
    }

    public LiveData<Resource<User>> getUserByUid(String uid) {
        userRepository.getUserByUID(uid, new LoadedDataCallback<User>() {
            @Override
            public void onSuccess(User result) {
                userMutableLiveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onFail(String error) {
                userMutableLiveData.setValue(new Resource<>(new User(), error));
            }
        });
        return userMutableLiveData;
    }

}
