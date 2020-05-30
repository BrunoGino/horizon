package br.com.horizon.ui.securities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.ui.securities.recyclerview.SecurityAdapter;
import br.com.horizon.ui.securities.viewmodel.SecurityListViewModel;

public class SecurityListFragment extends Fragment {
    private SecurityListViewModel securityListViewModel;
    private SecurityAdapter securityAdapter;
    private SecurityAdapter.OnItemClickListener onRecyclerItemClickListener;
    private NavController controller;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(true, true));

        return inflater.inflate(R.layout.security_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        securityListViewModel = ViewModelProviders.of(this).get(SecurityListViewModel.class);
        setupRecyclerView(view);
        pullSecurities(view);


        this.controller = Navigation.findNavController(view);
    }

    /**
     * Fetches the securities list from ViewModel and add its content to the adapter.
     *
     * @param view A view instance to show a Snackbar containing the error message if the fetch goes
     *             wrong.
     */
    private void pullSecurities(View view) {
        securityListViewModel.fetchAll().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.getData() != null) {
                securityAdapter.addAll(listResource.getData());
            } else {
                Snackbar.make(view, Objects.requireNonNull(listResource.getError()), 3000).show();
            }
        });
    }

    /**
     * Sets up the securities recycler view.
     *
     * @param view The view instance to host the recycler view component.
     */
    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.securities_recycler);
        setOnRecyclerItemClickListener();
        securityAdapter = new SecurityAdapter(view.getContext(), this.onRecyclerItemClickListener);
        recyclerView.setAdapter(securityAdapter);
    }

    private void setOnRecyclerItemClickListener() {
        this.onRecyclerItemClickListener = security -> goesToDetails(security.getId());
    }

    private void goesToDetails(String id) {
        SecurityListFragmentDirections
                .ActionSecurityListToSecurityDetailsFragment destination =
                SecurityListFragmentDirections
                        .actionSecurityListToSecurityDetailsFragment(id);

        controller.navigate(destination);
    }
}