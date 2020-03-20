package br.com.horizon.ui.securities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import br.com.horizon.R;
import br.com.horizon.ui.securities.recyclerview.SecurityAdapter;

public class SecurityFragment extends Fragment {

    private SecurityViewModel securityViewModel;
    private SecurityAdapter securityAdapter;
    private Dialog dialog;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        securityViewModel = ViewModelProviders.of(this).get(SecurityViewModel.class);
        View root = inflater.inflate(R.layout.fragment_securities, container, false);
        setupRecyclerView(root);

//        this.dialog = new Dialog(Objects.requireNonNull(getContext()));
//        dialog.setContentView(R.layout.dialog_title);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setupDialog();

        securityViewModel.getSecuritiesLiveData().observe(getViewLifecycleOwner(),
                securities -> securityAdapter.addAll(securities));

        return root;
    }

    private void setupDialog() {
    }

    private void setupRecyclerView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.securities_recycler);
        securityAdapter = new SecurityAdapter(root.getContext(), (position, security)
                -> dialog.show());
        recyclerView.setAdapter(securityAdapter);
    }


}