package br.com.horizon.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import br.com.horizon.R;
import br.com.horizon.databinding.FragmentProfileBinding;
import br.com.horizon.model.User;
import br.com.horizon.viewmodel.UserViewModel;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FragmentProfileBinding fragmentProfileBinding;
    private NavController navController;
    private UserViewModel userViewModel;
    private View.OnClickListener myFavoritesOnClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        return fragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        fragmentProfileBinding.setFavoritesListener(myFavoritesOnClickListener);

        String uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        userViewModel.getUser(uid).observe(getViewLifecycleOwner(), userResource -> {
            if (userResource.getData() != null) {
                User user = userResource.getData();
                fragmentProfileBinding.setUser(user);
            } else {
                Snackbar.make(view, R.string.could_not_fetch_profile, Snackbar.LENGTH_SHORT);
            }
        });

    }


}
