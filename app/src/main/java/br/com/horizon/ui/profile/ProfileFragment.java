package br.com.horizon.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import br.com.horizon.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FragmentProfileBinding fragmentProfileBinding;
    private NavController navController;
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
        return fragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        myFavoritesOnClickListener = v -> {
            NavDirections navDirections = ProfileFragmentDirections
                    .actionProfileFragmentToSecurityFavoritesFragment();
            navController.navigate(navDirections);
        };
        fragmentProfileBinding.setFavoritesListener(myFavoritesOnClickListener);

    }


}
