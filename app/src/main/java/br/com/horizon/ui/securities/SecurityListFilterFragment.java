package br.com.horizon.ui.securities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.List;

import br.com.horizon.databinding.SecurityListFilterBinding;
import br.com.horizon.viewmodel.FilterViewModel;


public class SecurityListFilterFragment extends Fragment {
    private SecurityListFilterBinding dataBinder;
    private NavController navController;
    private FilterViewModel filterViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinder = SecurityListFilterBinding.inflate(inflater, container, false);
        return dataBinder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel.class);

        dataBinder.setNavController(navController);

        List<String> strings = filterViewModel.getAllEmitters().getValue();
//        strings.forEach(s -> System.out.println(strings.indexOf(s)));

//        String[] arraySpinner = new String[strings.size()];
//        arraySpinner = strings.toArray(arraySpinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_dropdown_item, new String[]{"a", "b", "c", "d"});
        dataBinder.filtersEmitterValue.setAdapter(spinnerAdapter);


    }
}
