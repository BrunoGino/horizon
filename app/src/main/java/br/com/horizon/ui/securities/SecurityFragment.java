package br.com.horizon.ui.securities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import br.com.horizon.R;
import br.com.horizon.ui.securities.recyclerview.SecurityAdapter;

public class SecurityFragment extends Fragment {

    private SecurityViewModel securityViewModel;
    private SecurityAdapter securityAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        securityViewModel = ViewModelProviders.of(this).get(SecurityViewModel.class);
        View root = inflater.inflate(R.layout.fragment_securities, container, false);
        setupRecyclerView(root);

        //securityViewModel.getText().observe(getViewLifecycleOwner(), titleName::setText);
        securityViewModel.getSecuritiesLiveData().observe(getViewLifecycleOwner(),
                securities -> {
                    System.out.println(securities);
                    securityAdapter.addAll(securities);

                });

        return root;
    }

    private void setupRecyclerView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.securities_recycler);
        securityAdapter = new SecurityAdapter(root.getContext(), (position, security)
                -> Toast.makeText(recyclerView.getContext(), security.toString(), Toast.LENGTH_LONG).show());
        recyclerView.setAdapter(securityAdapter);
    }


}