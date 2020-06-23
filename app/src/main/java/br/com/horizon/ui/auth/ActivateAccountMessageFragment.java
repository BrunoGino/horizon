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

import com.google.android.material.snackbar.Snackbar;

import br.com.horizon.MainActivity;
import br.com.horizon.databinding.FragmentActivateAccountBinding;
import br.com.horizon.ui.VisualComponents;

public class ActivateAccountMessageFragment extends Fragment {
    private FragmentActivateAccountBinding fragmentActivateAccountBinding;
    private View.OnClickListener backToLoginListener;
    private View.OnClickListener sendActivationLinkAgainListener;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentActivateAccountBinding = FragmentActivateAccountBinding.inflate(inflater, container, false);
        return fragmentActivateAccountBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(false, false));
        navController = Navigation.findNavController(view);

        backToLoginListener = v -> goBackToLogin();
        sendActivationLinkAgainListener = v -> Snackbar.make(view, "To implement! :)", Snackbar.LENGTH_LONG).show();

        fragmentActivateAccountBinding.setBackToLoginListener(backToLoginListener);
        fragmentActivateAccountBinding.setSendLinkAgainListener(sendActivationLinkAgainListener);

        handleOnBackPressed();
    }

    private void handleOnBackPressed() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBackToLogin();
            }
        };
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    private void goBackToLogin() {
        NavDirections navDirections = ActivateAccountMessageFragmentDirections.actionGlobalLogin();
        navController.navigate(navDirections);
    }
}