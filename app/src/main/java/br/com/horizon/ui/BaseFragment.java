package br.com.horizon.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.horizon.ui.auth.LoginFragmentDirections;
import lombok.Getter;

public class BaseFragment extends Fragment {
    private NavController navController;
    @Getter
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        setupFirebaseListener();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        verifyIfUserIsLoggedIn();
    }

    private void verifyIfUserIsLoggedIn() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null || !currentUser.isEmailVerified()) {
            goesToLogin();
        }
    }

    private void goesToLogin() {
        NavDirections directions = LoginFragmentDirections.actionGlobalLogin();
        navController.navigate(directions);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void setupFirebaseListener() {
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                NavDirections actionGlobalLogin = BaseFragmentDirections.actionGlobalLogin();
                navController.navigate(actionGlobalLogin);
            }
        };
    }
}
