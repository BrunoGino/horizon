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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Pattern;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.FragmentRegisterBinding;
import br.com.horizon.model.User;
import br.com.horizon.repository.resource.Resource;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.viewmodel.UserViewModel;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding fragmentRegisterBinding;
    private UserViewModel userViewModel;
    private View.OnClickListener createAccountListener;
    private NavController navController;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        firebaseAuth = FirebaseAuth.getInstance();
        return fragmentRegisterBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(false, false));

        navController = Navigation.findNavController(view);

        createAccountListener = v -> handleUserCreation(view);

        fragmentRegisterBinding.setRegisterListener(createAccountListener);
    }

    private void handleUserCreation(View view) {
        if (inputsValid()) {
            String firstName = Objects.requireNonNull(fragmentRegisterBinding.textInputLayoutFirstName
                    .getEditText()).getText().toString();

            String lastName = Objects.requireNonNull(fragmentRegisterBinding.textInputLayoutLastName
                    .getEditText()).getText().toString();

            String email = Objects.requireNonNull(fragmentRegisterBinding.textInputLayoutEmail
                    .getEditText()).getText().toString();
            String password = Objects.requireNonNull(fragmentRegisterBinding.textInputLayoutPassword
                    .getEditText()).getText().toString();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = new User();
                    user.setName(firstName + " " + lastName);
                    user.setEmail(email);
                    String uid = Objects.requireNonNull(task.getResult()).getUser().getUid();
                    user.setUserUID(uid);
                    createNewUser(user);
                } else {
                    Snackbar.make(view, R.string.could_not_complete_registration, Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createNewUser(User user) {
        LiveData<Resource<User>> createdUser = userViewModel.createUser(user);
        createdUser.observe(getViewLifecycleOwner(), userResource -> {
            if (userResource.getData() != null) {
                sendEmailVerification();
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser currentUser = Objects.requireNonNull(firebaseAuth.getCurrentUser());
        currentUser.sendEmailVerification().addOnCompleteListener(emailTask -> {
            if (emailTask.isSuccessful()) {
                goToActivateAccountFragment();
            }
        });
    }

    private boolean inputsValid() {
        return validateFirstNameInput() && validateLastNameInput() && validateEmailInput()
                && validatePasswordInput() && validatePasswordConfirmationInput();
    }

    private boolean validateFirstNameInput() {
        TextInputLayout firstNameInputLayout = fragmentRegisterBinding.textInputLayoutFirstName;
        EditText firstNameEditText = Objects.requireNonNull(fragmentRegisterBinding
                .textInputLayoutFirstName.getEditText());
        String firstNameString = firstNameEditText.getText().toString();

        if (firstNameString.isEmpty() || firstNameString.trim().isEmpty()) {
            firstNameInputLayout.setError(getString(R.string.insert_your_name));
            return false;
        } else {
            firstNameInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateLastNameInput() {
        TextInputLayout lastNameInputLayout = fragmentRegisterBinding.textInputLayoutLastName;
        EditText lastNameEditText = Objects.requireNonNull(fragmentRegisterBinding.
                textInputLayoutLastName.getEditText());
        String lastNameString = lastNameEditText.getText().toString();

        if (lastNameString.isEmpty() || lastNameString.trim().isEmpty()) {
            lastNameInputLayout.setError(getString(R.string.insert_your_last_name));
            return false;
        } else {
            lastNameInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateEmailInput() {
        TextInputLayout emailInputLayout = fragmentRegisterBinding.textInputLayoutEmail;
        EditText emailEditText = Objects.requireNonNull(fragmentRegisterBinding
                .textInputLayoutEmail.getEditText());
        String emailString = emailEditText.getText().toString();

        if (emailString.isEmpty() || emailString.trim().isEmpty() || !isEmailValid(emailString)) {
            emailInputLayout.setError(getString(R.string.insert_a_valid_email));
            return false;
        } else {
            emailInputLayout.setError(null);
            return true;
        }
    }

    private boolean validatePasswordInput() {
        TextInputLayout passwordInputLayout = fragmentRegisterBinding.textInputLayoutPassword;
        EditText passwordEditText = Objects.requireNonNull(fragmentRegisterBinding
                .textInputLayoutPassword.getEditText());
        String passwordString = passwordEditText.getText().toString();
        if (passwordString.isEmpty() || passwordString.trim().isEmpty() || !isPasswordValid(passwordString)) {
            passwordInputLayout.setError(getString(R.string.invalid_password));
            return false;
        } else {
            passwordInputLayout.setError(null);
            return true;
        }
    }

    private boolean validatePasswordConfirmationInput() {
        TextInputLayout passwordConfirmInputLayout = fragmentRegisterBinding.textInputLayoutPasswordConfirm;
        EditText passwordConfirm = Objects.requireNonNull(fragmentRegisterBinding
                .textInputLayoutPasswordConfirm.getEditText());
        String passwordConfirmString = passwordConfirm.getText().toString();

        EditText passwordEditText = Objects.requireNonNull(fragmentRegisterBinding
                .textInputLayoutPassword.getEditText());
        String passwordString = passwordEditText.getText().toString();

        if (passwordString.isEmpty() || passwordString.trim().isEmpty()
                || !passwordConfirmString.equals(passwordString)) {
            passwordConfirmInputLayout.setError(getString(R.string.not_equal_to_password));
            return false;
        } else {
            passwordConfirmInputLayout.setError(null);
            return true;
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
