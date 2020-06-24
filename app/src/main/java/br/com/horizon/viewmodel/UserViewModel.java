package br.com.horizon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import br.com.horizon.model.User;
import br.com.horizon.repository.UserRepository;
import br.com.horizon.repository.callback.LoadedDataCallback;
import br.com.horizon.repository.resource.Resource;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private MutableLiveData<Resource<User>> userLiveData;

    public UserViewModel() {
        userRepository = new UserRepository();
        userLiveData = new MutableLiveData<>();
    }

    public LiveData<Resource<User>> createUser(User newUser){

    }

    public LiveData<Resource<User>> getUser(String uid) {
        userRepository.getUserByUID(uid, new LoadedDataCallback<User>() {
            @Override
            public void onSuccess(User result) {
                userLiveData.postValue(new Resource<>(result, null));
            }

            @Override
            public void onFail(String error) {
                userLiveData.postValue(new Resource<>(null,error));
            }
        });
        return userLiveData;
    }

}
