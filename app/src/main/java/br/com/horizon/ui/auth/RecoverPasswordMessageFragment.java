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
import com.google.firebase.auth.FirebaseAuth;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.FragmentRecoverMessageBinding;
import br.com.horizon.ui.VisualComponents;

public class RecoverPasswordMessageFragment extends Fragment {
    private FragmentRecoverMessageBinding recoverMessageBinding;
    private View.OnClickListener backToLoginListener;
    private View.OnClickListener sendRecoveryLinkAgainListener;
    private NavController navController;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recoverMessageBinding = FragmentRecoverMessageBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        return recoverMessageBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(false, false));

        navController = Navigation.findNavController(view);

        backToLoginListener = v -> goBackToLogin();
        sendRecoveryLinkAgainListener = v -> resendPasswordRecoverEmail(view);

        recoverMessageBinding.setBackToLoginListener(backToLoginListener);
        recoverMessageBinding.setSendRecoveryLinkAgainListener(sendRecoveryLinkAgainListener);

        handleOnBackPressed();
    }

    private void resendPasswordRecoverEmail(View view) {
        String userEmail = RecoverPasswordMessageFragmentArgs.fromBundle(requireArguments()).getUserEmail();
        firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Snackbar.make(view, getString(R.string.recovery_email_resent) + userEmail, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goBackToLogin() {
        NavDirections navDirections = RecoverPasswordMessageFragmentDirections
                .actionGlobalLogin();

        navController.navigate(navDirections);
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
}