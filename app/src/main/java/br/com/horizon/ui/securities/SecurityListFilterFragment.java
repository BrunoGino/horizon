package br.com.horizon.ui.securities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;

import br.com.horizon.R;
import br.com.horizon.databinding.SecurityListFilterBinding;
import br.com.horizon.model.Filter;
import br.com.horizon.viewmodel.FilterViewModel;


public class SecurityListFilterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private SecurityListFilterBinding dataBinder;
    private NavController navController;
    private NumberFormat percentageFormatter;
    private FilterViewModel filterViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        percentageFormatter = NumberFormat.getPercentInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinder = SecurityListFilterBinding.inflate(inflater, container, false);
        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel.class);
        return dataBinder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        dataBinder.setNavController(navController);

        filterViewModel.getEmitters().observe(getViewLifecycleOwner(), strings -> {
            String[] arraySpinner = new String[strings.size()];
            arraySpinner = strings.toArray(arraySpinner);

            ArrayAdapter<String> emittersAdapter = new ArrayAdapter<>(view.getContext(),
                    android.R.layout.simple_spinner_dropdown_item, arraySpinner);
            dataBinder.filtersEmitterValue.setAdapter(emittersAdapter);
        });

        filterViewModel.getPublishers().observe(getViewLifecycleOwner(), strings -> {
            String[] arraysSpinner = new String[strings.size()];
            arraysSpinner = strings.toArray(arraysSpinner);

            ArrayAdapter<String> publishersAdapter = new ArrayAdapter<>(view.getContext(),
                    android.R.layout.simple_spinner_dropdown_item, arraysSpinner);
            dataBinder.filtersPublisherValue.setAdapter(publishersAdapter);
        });

        ArrayAdapter<String> incomeTypesAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_dropdown_item, new String[]{getString(R.string.daily),
                getString(R.string.semiannual), getString(R.string.on_expiry)});

        dataBinder.filtersIncomeTypeValue.setAdapter(incomeTypesAdapter);

        ArrayAdapter<String> irTaxesAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_dropdown_item, new String[]{
                percentageFormatter.format(0.20), percentageFormatter.format(0.175),
                percentageFormatter.format(0.15), getString(R.string.no_fee)
        });

        dataBinder.filtersIrValue.setAdapter(irTaxesAdapter);

        filterViewModel.getMinInterest().observe(getViewLifecycleOwner(), minInterest ->
                filterViewModel.getMaxInterest().observe(getViewLifecycleOwner(), maxInterest ->
                        dataBinder.filterRangeValueGrossInterest.setRangeValues(minInterest, maxInterest)));

        dataBinder.filtersEndingDateValue.setOnFocusChangeListener((v, hasFocus) -> showDatePickerDialog(v));

        Filter filter = new Filter();
        filter.setEmitter(dataBinder.filtersEmitterValue.toString());
        filter.setPublisher(dataBinder.filtersPublisherValue.toString());
        filter.setIncomeType(dataBinder.filtersIncomeTypeValue.toString());
        filter.setLiquidity(dataBinder.filtersLiquidityValue.toString());
        filter.setInterestMin((Double) dataBinder.filterRangeValueGrossInterest.getSelectedMinValue());
        filter.setInterestMax((Double) dataBinder.filterRangeValueGrossInterest.getSelectedMaxValue());
        filter.setEndingDate(dataBinder.filtersEndingDateValue.getText());
        filter.setIrValue(parseIrValue(dataBinder.filtersIrValue.toString()));
        filter.setFgc(dataBinder.filtersFgcValue.isChecked());
        dataBinder.filtersApplyButton.setOnClickListener(v -> {
            NavDirections toListFragment = SecurityListFilterFragmentDirections
                    .actionSecurityListFilterFragmentToSecuritiesListFragment(filter);
            navController.navigate(toListFragment);
        });

    }

    private Double parseIrValue(String irStringValue) {
        switch (irStringValue) {
            case "20%":
                return 0.2;
            case "18%":
                return 0.18;
            case "15%":
                return 0.15;
            default:
                return 0.0;
        }
    }

    private void showDatePickerDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);
        dataBinder.filtersEndingDateValue.setText(dateTimeFormatter.format(localDate));
    }
}
