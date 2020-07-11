package br.com.horizon.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.FragmentHomeBinding;
import br.com.horizon.model.User;
import br.com.horizon.ui.BaseFragment;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.ui.home.recyclerview.TypeCardAdapter;
import br.com.horizon.viewmodel.HomeViewModel;

public class HomeFragment extends BaseFragment {
    private FragmentHomeBinding viewBinder;
    private HomeViewModel homeViewModel;
    private NavController controller;
    private TypeCardAdapter.OnItemClickListener onRecyclerItemClickListener;
    private List<String> titleTypes;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        currentUser = getFirebaseAuth().getCurrentUser();
        titleTypes = new ArrayList<>(Arrays.asList(getString(R.string.public_titles),
                getString(R.string.cdb), getString(R.string.lci_lca), getString(R.string.cri_cra),
                getString(R.string.debentures), getString(R.string.all_titles)));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinder = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        return viewBinder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(true, false));

        controller = Navigation.findNavController(view);
        setupRecyclerView(view);

        viewBinder.tvAbout.setOnClickListener(v -> {
            NavDirections navDirection = HomeFragmentDirections.actionHomeFragmentToAboutFragment();
            controller.navigate(navDirection);
        });
        viewBinder.tvFaq.setOnClickListener(v -> {
            NavDirections navDirection = HomeFragmentDirections.actionHomeFragmentToFAQFragment();
            controller.navigate(navDirection);
        });
        viewBinder.btIndexes.setOnClickListener(v -> {
            NavDirections navDirection = HomeFragmentDirections.actionHomeFragmentToIndexesFragment();
            controller.navigate(navDirection);
        });

        if (currentUser != null) {
            homeViewModel.getUserByUid(currentUser.getUid()).observe(getViewLifecycleOwner(), userResource -> {
                if (userResource.getData() != null) {
                    User user = userResource.getData();
                    String userName = currentUser.getDisplayName() == null ?
                            splitUserName(user.getName()) : splitUserName(currentUser.getDisplayName());
                    viewBinder.tvWelcomeMessage.setText(getString(R.string.welcome, userName));
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.user_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(requireContext(),
                    R.style.ThemeOverlay_App_MaterialAlertDialog);

            builder.setMessage(getString(R.string.would_you_like_to_sign_out));
            builder.setPositiveButton(getString(R.string.yes), (arg0, arg1) ->
                    getFirebaseAuth().signOut());
            builder.setNegativeButton(getString(R.string.no), (dialog, which) -> {

            });
            builder.show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = viewBinder.titleTypesRecycler;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        setOnRecyclerItemClickListener();
        TypeCardAdapter typeCardAdapter = new TypeCardAdapter(view.getContext(), this.onRecyclerItemClickListener);
        typeCardAdapter.setHasStableIds(true);
        recyclerView.setAdapter(typeCardAdapter);
        typeCardAdapter.addAll(titleTypes);
        typeCardAdapter.notifyDataSetChanged();
    }

    private void setOnRecyclerItemClickListener() {
        onRecyclerItemClickListener = securityType -> {
            if (securityType.equals("Tesouro Direto")) {
                goesToListFilteredByType("TD");
            } else {
                goesToListFilteredByType(securityType);
            }
        };
    }

    private void goesToListFilteredByType(String titleType) {
        NavDirections direction = HomeFragmentDirections
                .actionHomeFragmentToSecuritiesListFragment(titleType);

        controller.navigate(direction);
    }

    private String splitUserName(String userName) {
        return userName.split(" ")[0];
    }

}

