package br.com.horizon.ui.auth;

import android.os.Bundle;
import android.util.Patterns;
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

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
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

        createAccountListener = v -> handleUserCreation();

        fragmentRegisterBinding.setRegisterListener(createAccountListener);
    }


    private void handleUserCreation() {
        validateFirstNameInput();
        validateLastNameInput();
        validateEmailInput();
        validatePasswordInput();

    }

    private void validateFirstNameInput() {
        TextInputLayout firstNameInputLayout = fragmentRegisterBinding.textInputLayoutFirstName;
        EditText firstNameEditText = Objects.requireNonNull(fragmentRegisterBinding
                .textInputLayoutFirstName.getEditText());
        String firstNameString = firstNameEditText.getText().toString();

        if (firstNameString.isEmpty() || firstNameString.trim().isEmpty()) {
            firstNameInputLayout.setErrorEnabled(true);
            firstNameInputLayout.setError(getString(R.string.insert_your_name));
        } else {
            firstNameInputLayout.setErrorEnabled(false);
        }
    }

    private void validateLastNameInput() {
        TextInputLayout lastNameInputLayout = fragmentRegisterBinding.textInputLayoutLastName;
        EditText lastNameEditText = Objects.requireNonNull(fragmentRegisterBinding.
                textInputLayoutLastName.getEditText());
        String lastNameString = lastNameEditText.getText().toString();

        if (lastNameString.isEmpty() || lastNameString.trim().isEmpty()) {
            lastNameInputLayout.setErrorEnabled(true);
            lastNameInputLayout.setError(getString(R.string.insert_your_last_name));
        } else {
            lastNameInputLayout.setErrorEnabled(false);
        }
    }

    private void validateEmailInput() {
        TextInputLayout emailInputLayout = fragmentRegisterBinding.textInputLayoutEmail;
        EditText emailEditText = Objects.requireNonNull(fragmentRegisterBinding
                .textInputLayoutEmail.getEditText());
        String emailString = emailEditText.getText().toString();

        if (emailString.isEmpty() || emailString.trim().isEmpty() || !isEmailValid(emailString)) {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError(getString(R.string.insert_a_valid_email));
        } else {
            emailInputLayout.setErrorEnabled(false);
        }
    }

    private void validatePasswordInput() {
        TextInputLayout passwordInputLayout = fragmentRegisterBinding.textInputLayoutPassword;
        EditText passwordEditText = Objects.requireNonNull(fragmentRegisterBinding
                .textInputLayoutPassword.getEditText());
        String passwordString = passwordEditText.getText().toString();
        if (passwordString.isEmpty() || passwordString.trim().isEmpty() || !isPasswordValid(passwordString)) {

            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError(getString(R.string.invalid_password));
        } else {
            passwordInputLayout.setErrorEnabled(false);
        }
    }

    private void goToActivateAccountFragment() {
        NavDirections navDirections = RegisterFragmentDirections
                .actionRegisterFragmentToActivateAccountMessageFragment();

        navController.navigate(navDirections);
    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6 && hasACapitalLetterAndNumbers(password);
    }

    private boolean hasACapitalLetterAndNumbers(String password) {
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isDigit(ch)) {
                numberFlag = true;
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if (numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }


}
