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
import br.com.horizon.databinding.FragmentForgotPasswordBinding;
import br.com.horizon.ui.VisualComponents;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding forgotPasswordBinding;
    private View.OnClickListener findAccountListener;
    private NavController navController;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        forgotPasswordBinding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return forgotPasswordBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(false, false));

        navController = Navigation.findNavController(view);

        findAccountListener = v -> {
            NavDirections navDirections = ForgotPasswordFragmentDirections
                    .actionForgotPasswordFragmentToRecoverPasswordFragment();

            navController.navigate(navDirections);
        };
        forgotPasswordBinding.setFindAccountListener(findAccountListener);
        handleOnBackPressed();
    }

    private void handleOnBackPressed() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavDirections navDirections = ForgotPasswordFragmentDirections.actionGlobalLogin();
                navController.navigate(navDirections);
            }
        };

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }
}
