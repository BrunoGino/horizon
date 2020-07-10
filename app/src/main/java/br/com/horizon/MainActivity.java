package br.com.horizon;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import br.com.horizon.viewmodel.AppStateViewModel;
import io.fabric.sdk.android.Fabric;
import lombok.Getter;

public class MainActivity extends AppCompatActivity {

    private static AppStateViewModel appStateViewModel = new AppStateViewModel();
    private NavController controller;
    @Getter
    private BottomNavigationView bottomNavigationView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = Navigation.findNavController(this, R.id.nav_host_fragment);
        actionBar = Objects.requireNonNull(getSupportActionBar());
        bottomNavigationView = findViewById(R.id.nav_view);
        setupBottomNavigationView();
        setupOnDestinationChangeListener();
        Fabric.with(this, new Crashlytics());
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupOnDestinationChangeListener() {
        controller.addOnDestinationChangedListener((controller1, destination, arguments) -> {
            actionBar.setTitle(destination.getLabel());
            appStateViewModel.getComponents().observe(this, visualComponents -> {
                if (visualComponents.hasAppBar()) {
                    actionBar.show();
                } else {
                    actionBar.hide();
                }

                if (visualComponents.hasBottomNav()) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                } else {
                    bottomNavigationView.setVisibility(View.GONE);
                }
            });
        });
    }

    private void setupBottomNavigationView() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.loginFragment,
                R.id.homeFragment)
                .build();

        NavigationUI.setupActionBarWithNavController(this, controller, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, controller);
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    public static AppStateViewModel getAppStateViewModel() {
        return appStateViewModel;
    }

}
