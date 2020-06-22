package br.com.horizon.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import br.com.horizon.MainActivity;
import br.com.horizon.databinding.FragmentRegisterBinding;
import br.com.horizon.ui.VisualComponents;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding fragmentRegisterBinding;
    private View.OnClickListener createAccountListener;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        return fragmentRegisterBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(false, false));

        navController = Navigation.findNavController(view);

        createAccountListener = v -> goToActivateAccountFragment();

        fragmentRegisterBinding.setRegisterListener(createAccountListener);
    }

    private void goToActivateAccountFragment() {
        NavDirections navDirections = RegisterFragmentDirections
                .actionRegisterFragmentToActivateAccountMessageFragment();

        navController.navigate(navDirections);
    }
}
