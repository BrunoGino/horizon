package br.com.horizon.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import br.com.horizon.R;
import br.com.horizon.ui.BaseFragment;

public class FavoritesFragment extends BaseFragment {

    private FavoritesViewModel favoritesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favoritesViewModel =
                ViewModelProviders.of(this).get(FavoritesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
        favoritesViewModel.getText().observe(getViewLifecycleOwner(), s -> {
            //textView.setText(s);
        });
        return root;
    }
}