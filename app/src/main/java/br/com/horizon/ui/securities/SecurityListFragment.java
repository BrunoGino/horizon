package br.com.horizon.ui.securities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.SecurityListBinding;
import br.com.horizon.model.Security;
import br.com.horizon.repository.resource.Resource;
import br.com.horizon.ui.BaseFragment;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.ui.securities.recyclerview.SecurityAdapter;
import br.com.horizon.viewmodel.SecurityListViewModel;

public class SecurityListFragment extends BaseFragment {
    private SecurityListViewModel securityListViewModel;
    private SecurityAdapter securityAdapter;
    private SecurityAdapter.OnItemClickListener onRecyclerItemClickListener;
    private NavController controller;
    private SecurityListBinding securityListBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        securityListBinding = SecurityListBinding.inflate(inflater, container, false);

        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(true, false));

        return securityListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        securityListViewModel = ViewModelProviders.of(this).get(SecurityListViewModel.class);
        setupRecyclerView(view);
        pullSecurities();
        this.controller = Navigation.findNavController(view);
        handleOnBackPressed();
    }

    private void handleOnBackPressed() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                controller.popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
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
        securityListBinding.setSecurityAdapter(securityAdapter);
        securityAdapter.setHasStableIds(true);
        recyclerView.setAdapter(securityAdapter);
    }

    /**
     * Fetches the securities list from ViewModel and add its content to the adapter.
     */
    private void pullSecurities() {
        String titleType = SecurityListFragmentArgs.fromBundle(requireArguments()).getTitleType();
        boolean orderByInterest = SecurityListFragmentArgs.fromBundle(requireArguments()).getOrderByInterest();
        boolean orderByMostFavorite = SecurityListFragmentArgs.fromBundle(requireArguments()).getOrderByMostFavorite();
        if (!titleType.isEmpty()) {
            getSecuritiesByType(titleType);
        }
        if (orderByInterest) {
            getFirstHundredOrderedByInterest();
        }

        if (orderByMostFavorite) {
            getAllMostFavorite();
        }
    }

    private void getAllMostFavorite() {
        securityListViewModel.fetchMostFavorited();
    }

    private void getFirstHundredOrderedByInterest() {
        securityListViewModel.fetchFirstHundredWithGreatestInterest().observe(getViewLifecycleOwner(), listResource ->
                updateViewWithObtainedData(listResource.getData()));
    }

    private void getSecuritiesByType(String titleType) {
        securityListViewModel.fetchFilteredByType(titleType).observe(getViewLifecycleOwner(), listResource ->
                updateViewWithObtainedData(listResource.getData()));
    }

    private void updateViewWithObtainedData(List<Security> listData) {
        if (listData != null && !listData.isEmpty()) {
            securityListBinding.listFragmentIfNoValues.setVisibility(View.GONE);
            securityAdapter.addAll(listData);
            securityAdapter.notifyDataSetChanged();
        } else {
            securityListBinding.listFragmentIfNoValues.setVisibility(View.VISIBLE);
        }
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