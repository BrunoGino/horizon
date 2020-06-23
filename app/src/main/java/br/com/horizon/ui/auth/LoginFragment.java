package br.com.horizon.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.FragmentLoginBinding;
import br.com.horizon.ui.VisualComponents;

public class LoginFragment extends Fragment {
    private static final int G_SIGN_IN = 9001;
    private FirebaseAuth firebaseAuth;
    private FragmentLoginBinding loginFragmentBinding;
    private NavController navController;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions googleSignInOptions;
    private View.OnClickListener registerClickListener;
    private View.OnClickListener forgotPassClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginFragmentBinding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = loginFragmentBinding.getRoot();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(false, false));

        navController = Navigation.findNavController(view);

        setOnClickListeners(view);
    }

    private void setOnClickListeners(@NonNull View view) {
        forgotPassClickListener = v -> goToForgotPasswordFragment();
        loginFragmentBinding.setForgotPassClickListener(forgotPassClickListener);

        registerClickListener = v -> goToRegisterFragment();
        loginFragmentBinding.setRegisterClickListener(registerClickListener);

        Objects.requireNonNull(loginFragmentBinding.txtPassword.getEditText()).setOnClickListener(v ->
                loginFragmentBinding.txtPassword.setErrorEnabled(false));

        Objects.requireNonNull(loginFragmentBinding.txtEmail.getEditText()).setOnClickListener(v ->
                loginFragmentBinding.txtEmail.setErrorEnabled(true));

        loginFragmentBinding.btGoogleSignIn.setOnClickListener(v -> startGoogleSignInDialog());

        loginFragmentBinding.btLogin.setOnClickListener(v -> {

            TextInputLayout emailInputLayout = loginFragmentBinding.txtEmail;
            EditText emailEditText = Objects.requireNonNull(emailInputLayout.getEditText());

            TextInputLayout passwordInputLayout = loginFragmentBinding.txtPassword;
            EditText passwordEditText = Objects.requireNonNull(passwordInputLayout.getEditText());


            if (!passwordEditText.getText().toString().isEmpty() &&
                    !emailEditText.getText().toString().isEmpty()) {

                String passwordString = passwordEditText.getText().toString();
                String emailString = emailEditText.getText().toString();

                loginFragmentBinding.txtEmail.setErrorEnabled(false);
                loginFragmentBinding.txtPassword.setErrorEnabled(false);
                authenticateWithEmailAndPassword(view, emailString, passwordString);
            } else {
                showErrorInfoForInputs(emailEditText, passwordEditText);
            }
        });
    }

    private void showErrorInfoForInputs(EditText emailEditText,
                                        EditText passwordEditText) {
        if (emailEditText.getText().toString().isEmpty()) {
            displayValidationErrorForEmailInput();
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            displayValidationErrorForPasswordInput();
        }
    }

    private void startGoogleSignInDialog() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, G_SIGN_IN);
    }

    private void goToRegisterFragment() {
        NavDirections navDirections = LoginFragmentDirections
                .actionLoginFragmentToRegisterFragment();

        navController.navigate(navDirections);
    }

    private void goToForgotPasswordFragment() {
        NavDirections navDirections = LoginFragmentDirections
                .actionLoginFragmentToForgotPasswordFragment();

        navController.navigate(navDirections);
    }

    private void displayValidationErrorForEmailInput() {
        loginFragmentBinding.txtEmail.setErrorEnabled(true);
        loginFragmentBinding.txtEmail.setError(getString(R.string.insert_email_error));
    }

    private void displayValidationErrorForPasswordInput() {
        loginFragmentBinding.txtPassword.setErrorEnabled(true);
        loginFragmentBinding.txtPassword.setError(getString(R.string.insert_pass_error));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == G_SIGN_IN) {
            Task<GoogleSignInAccount> signInIntent = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleAsyncGoogleSignInIntent(signInIntent);
        }
    }

    private void handleAsyncGoogleSignInIntent(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount result = completedTask.getResult(ApiException.class);
            authenticateWithGoogle(Objects.requireNonNull(result));
        } catch (ApiException e) {
            Snackbar.make(loginFragmentBinding.getRoot(), getString(R.string.google_signin_error),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void authenticateWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                navigateToHomeScreen();
            } else {
                Snackbar.make(loginFragmentBinding.getRoot(), getString(R.string.invalid_credentials),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToHomeScreen() {
        Log.d("PASS_HOME_SCREEN", "You reached me!! :) navigateToHomeScreen");
        NavDirections navDirections = LoginFragmentDirections.actionLoginFragmentToHomeFragment();
        navController.navigate(navDirections);
    }

    private void authenticateWithEmailAndPassword(View view, @NonNull String email, @NonNull String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                    navigateToHomeScreen();
                } else {
                    Snackbar.make(view, getString(R.string.verify_inbox), Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(view, getString(R.string.wrong_email_or_pass), Snackbar.LENGTH_LONG).show();
            }
        });
    }

}
