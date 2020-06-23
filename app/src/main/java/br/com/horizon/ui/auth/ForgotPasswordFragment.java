package br.com.horizon.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.FragmentForgotPasswordBinding;
import br.com.horizon.ui.VisualComponents;

public class ForgotPasswordFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FragmentForgotPasswordBinding forgotPasswordBinding;
    private View.OnClickListener findAccountListener;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

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

        findAccountListener = v -> handlePasswordRecovery();
        forgotPasswordBinding.setFindAccountListener(findAccountListener);
        handleOnBackPressed();
    }

    private void handlePasswordRecovery() {
        EditText emailEditText = Objects.requireNonNull(forgotPasswordBinding
                .textInputLayoutEmailRecovery.getEditText());
        String userEmail = emailEditText.getText().toString();
        if (!userEmail.isEmpty()) {
            forgotPasswordBinding.textInputLayoutEmailRecovery.setErrorEnabled(false);
            firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    goToForgotPasswordFragment(userEmail);
                } else {
                    forgotPasswordBinding.textInputLayoutEmailRecovery.setErrorEnabled(true);
                    forgotPasswordBinding.textInputLayoutEmailRecovery.setError(getString(R.string.invalid_email));
                }
            });
        } else {
            forgotPasswordBinding.textInputLayoutEmailRecovery.setErrorEnabled(true);
            forgotPasswordBinding.textInputLayoutEmailRecovery.setError(getString(R.string.email_cant_be_null));
        }
    }

    private void goToForgotPasswordFragment(String userEmail) {
        NavDirections navDirections = ForgotPasswordFragmentDirections
                .actionForgotPasswordFragmentToRecoverPasswordFragment(userEmail);

        navController.navigate(navDirections);
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
