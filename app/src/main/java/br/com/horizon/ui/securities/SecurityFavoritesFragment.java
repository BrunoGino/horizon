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

import java.util.List;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.FragmentFavoritesListBinding;
import br.com.horizon.model.Security;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.ui.securities.recyclerview.SecurityAdapter;
import br.com.horizon.viewmodel.SecurityFavoritesViewModel;

public class SecurityFavoritesFragment extends Fragment {
    private SecurityFavoritesViewModel securityFavoritesViewModel;
    private SecurityAdapter securityAdapter;
    private SecurityAdapter.OnItemClickListener onRecyclerItemClickListener;
    private NavController navController;
    private FragmentFavoritesListBinding securityFavoritesBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        securityFavoritesBinding = FragmentFavoritesListBinding.inflate(inflater, container, false);

        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(true, false));

        return securityFavoritesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        securityFavoritesViewModel = ViewModelProviders.of(this).get(SecurityFavoritesViewModel.class);
        setupRecyclerView(view);
        pullSecurities();
        this.navController = Navigation.findNavController(view);
    }

    /**
     * Sets up the securities recycler view.
     *
     * @param view The view instance to host the recycler view component.
     */
    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.securities_recycler_favorites);
        setOnRecyclerItemClickListener();
        securityAdapter = new SecurityAdapter(view.getContext(), this.onRecyclerItemClickListener);
        securityFavoritesBinding.setSecurityAdapter(securityAdapter);
        securityAdapter.setHasStableIds(true);
        recyclerView.setAdapter(securityAdapter);
    }

    /**
     * Fetches the securities list from ViewModel and add its content to the adapter.
     */
    private void pullSecurities() {

            getMyFavorites();

    }

    private void getMyFavorites() {
        securityFavoritesViewModel.getMyFavorites();
    }

    private void updateViewWithObtainedData(List<Security> listData) {
        if (listData != null && !listData.isEmpty()) {
            securityAdapter.addAll(listData);
            securityAdapter.notifyDataSetChanged();
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

        navController.navigate(destination);
    }
}
