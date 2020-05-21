package br.com.horizon;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import br.com.horizon.ui.securities.viewmodel.AppStateViewModel;

public class MainActivity extends AppCompatActivity {

    private static AppStateViewModel appStateViewModel = new AppStateViewModel();
    private NavController controller;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = Navigation.findNavController(this, R.id.nav_host_fragment);
        setupBottomNavigationView();
        setupOnDestinationChangeListener();

    }

    private void setupOnDestinationChangeListener() {
        controller.addOnDestinationChangedListener((controller1, destination, arguments) -> {
            getSupportActionBar().setTitle(destination.getLabel());
            appStateViewModel.getComponents().observe(this, visualComponents -> {
                if (visualComponents.hasAppBar()) {
                    getSupportActionBar().show();
                } else {
                    getSupportActionBar().hide();
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
        bottomNavigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.securitiesListFragment, R.id.walletFragment)
                .build();


        NavigationUI.setupActionBarWithNavController(this, controller, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, controller);
    }

    public static AppStateViewModel getAppStateViewModel() {
        return appStateViewModel;
    }
}
