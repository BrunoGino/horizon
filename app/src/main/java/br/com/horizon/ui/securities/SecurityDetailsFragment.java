package br.com.horizon.ui.securities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.model.GradientColor;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.SecurityDetailsBinding;
import br.com.horizon.model.Security;
import br.com.horizon.ui.BaseFragment;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.ui.databinding.ObservableSecurity;
import br.com.horizon.viewmodel.SecurityDetailsViewModel;

public class SecurityDetailsFragment extends BaseFragment {
    private SecurityDetailsBinding dataBinder;
    private NumberFormat currencyFormatter;
    private NumberFormat percentageFormatter;
    private MutableLiveData<Double> simulateValue;
    private NavController navController;
    private ObservableSecurity observableSecurity;
    private SecurityDetailsViewModel securityDetailsViewModel;
    private int graphTextColor;
    private int taxColor;
    private int grossIncomeColor;
    private int liquidIncomeColor;
    private int liquidAmountColor;
    private BarChart chart;


    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        securityDetailsViewModel = ViewModelProviders.of(this).get(SecurityDetailsViewModel.class);
        currencyFormatter = NumberFormat.getCurrencyInstance();
        percentageFormatter = NumberFormat.getPercentInstance();
        percentageFormatter.setMaximumFractionDigits(2);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinder = SecurityDetailsBinding.inflate(inflater, container, false);
        View rootView = dataBinder.getRoot();
        simulateValue = new MutableLiveData<>();
        setupChart(rootView);
        String securityId = SecurityDetailsFragmentArgs.fromBundle(requireArguments()).getSecurityId();
        securityDetailsViewModel.getLiveDataById(securityId).observe(getViewLifecycleOwner(), securityResource -> {
            if (securityResource.getData() != null) {
                Security security = securityResource.getData();
                observableSecurity = new ObservableSecurity(security);
                dataBinder.setSecurity(observableSecurity);
                simulateValue.setValue(security.getTitleValue());
                applyValuesToDataBinder(security);
                updateChartWithSimulateValue(security.getTitleValue());
            } else {
                showFetchError(rootView);
            }
        });
        return rootView;
    }

    private void setupChart(View rootView) {
        instantiateChartColors(rootView);
        chart = createBarChart(rootView);
        setupChartLegend(chart);
    }

    private void showFetchError(View root) {
        Snackbar.make(root, R.string.fetch_error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(true, false));

        navController = Navigation.findNavController(view);
        handleOnBackPressed();
    }

    private void handleOnBackPressed() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    private void updateChartWithSimulateValue(Double minValue) {
        simulateValue.observe(getViewLifecycleOwner(), aDouble -> {
            Double simulateValue = this.simulateValue.getValue();
            if (simulateValue >= minValue) {
                applyAnnualIncomeValues(observableSecurity.toSecurity());
                updateChartDataWithSimulateValue(chart);
            } else {
                setErrorEditTextWhenSimulateValueIsLessThan(minValue);
            }
        });
    }

    private void applyValuesToDataBinder(Security security) {
        dataBinder.setLifecycleOwner(this);
        dataBinder.setSecurity(observableSecurity);
        dataBinder.setSimulateValue(simulateValue);
        dataBinder.setCurrencyFormatter(currencyFormatter);
        dataBinder.setPercentageFormatter(percentageFormatter);
        dataBinder.setUrlClick(v -> redirectsToBrowserIfUrlIsValid());
        dataBinder.securityDetailIncomeTaxYearlyGrossValue.setText(
                percentageFormatter.format(security.getInterest() / 100));
        dataBinder.securityDetailIncomeTaxYearlyLiquidValue.setText(parseYearlyLiquidInterest(security));
        applyAnnualIncomeValues(security);
        dataBinder.securityDetailLiquidityValue.setText(parseLiquidity(security.getLiquidity()));
        dataBinder.securityDetailInterestTypeValue.setText(parseInterestType(security.getInterestType()));
        dataBinder.securityDetailIncomeTypeValue.setText(parseIncomeType(security.getLiquidity(), security.getTitleName()));
        dataBinder.securityDetailTaxValue.setText(parseTotalTaxPercentage(observableSecurity));
    }

    private void applyAnnualIncomeValues(Security security) {
        dataBinder.securityDetailAnnualGrossIncomeValue.setText(
                parseAnnualIncome(security)
        );
        dataBinder.securityDetailAnnualLiquidIncomeValue.setText(
                parseAnnualIncome(security)
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private String parseLiquidity(Integer liquidity) {
        if (liquidity == 1) {
            return "D+1";
        }
        return getString(R.string.on_expiry);
    }

    private String parseAnnualIncome(Security security) {

        if (security.getIr()) {
            return currencyFormatter.format(security.getLiquidAnnualIncome(simulateValue.getValue()));
        }

        return currencyFormatter.format(security.getGrossAnnualIncome(simulateValue.getValue()));
    }

    private String parseYearlyLiquidInterest(Security security) {

        if (security.getIr()) {
            return percentageFormatter.format(security.getLiquidAnnualInterest(simulateValue.getValue()) / 100);
        }

        return percentageFormatter.format(security.getInterest() / 100);
    }

    private String parseTotalTaxPercentage(ObservableSecurity observableSecurity) {
        if (observableSecurity.toSecurity().getIr()) {
            return percentageFormatter.format(observableSecurity.getTotalIrTaxPercentage().getValue());
        }
        return getString(R.string.no_fee);
    }

    private void setErrorEditTextWhenSimulateValueIsLessThan(Double minInvValue) {
        dataBinder.securityDetailSimulateValueText.requestFocus();
        dataBinder.securityDetailSimulateValue.setErrorEnabled(true);
        dataBinder.securityDetailSimulateValue.setError(getString(R.string.insertAValueGreaterOrEqualTo)
                + " " + currencyFormatter.format(minInvValue));
    }

    private void updateChartDataWithSimulateValue(BarChart chart) {
        updateChartDataSet(observableSecurity.toSecurity(), chart);
        dataBinder.securityDetailSimulateValue.setErrorEnabled(false);
    }

    private void updateChartDataSet(Security security, BarChart chart) {
        BarDataSet barDataSet = new BarDataSet(addDataValues(security), "");
        barDataSet.setGradientColors(setupBarColors());
        barDataSet.setValueTextColor(graphTextColor);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        chart.setData(barData);
        chart.invalidate();
    }

    private void instantiateChartColors(@NonNull View view) {
        graphTextColor = ContextCompat.getColor(view.getContext(), R.color.white);
        taxColor = ContextCompat.getColor(view.getContext(), R.color.graphTax);
        grossIncomeColor = ContextCompat.getColor(view.getContext(), R.color.graphInterest);
        liquidIncomeColor = ContextCompat.getColor(view.getContext(), R.color.graphInterestLiq);
        liquidAmountColor = ContextCompat.getColor(view.getContext(), R.color.graphLiquidIncome);
    }

    private void setupChartLegend(BarChart chart) {
        Legend chartLegend = chart.getLegend();

        List<LegendEntry> legends = new ArrayList<>();
        legends.add(new LegendEntry(getString(R.string.tax), Legend.LegendForm.CIRCLE,
                10f, 10f, null, taxColor));
        legends.add(new LegendEntry(getString(R.string.graphGrossIncome), Legend.LegendForm.CIRCLE,
                10f, 10f, null, grossIncomeColor));
        legends.add(new LegendEntry(getString(R.string.graphLiquidIncome), Legend.LegendForm.CIRCLE,
                10f, 10f, null, liquidIncomeColor));
        legends.add(new LegendEntry(getString(R.string.graphLiquidAmount), Legend.LegendForm.CIRCLE,
                10f, 10f, null, liquidAmountColor));
        chartLegend.setCustom(legends);

        chartLegend.setTextSize(12f);
        chartLegend.setTextColor(graphTextColor);
        chartLegend.setYEntrySpace(5f);
        chartLegend.setFormToTextSpace(5f);
    }

    private BarChart createBarChart(@NonNull View view) {
        BarChart chart = dataBinder.simulationPlot;
        chart.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.graphBackground));
        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.getAxisLeft().setTextColor(graphTextColor);

        return chart;
    }

    private List<GradientColor> setupBarColors() {
        List<GradientColor> gradientColors = new ArrayList<>();
        gradientColors.add(new GradientColor(taxColor, taxColor));
        gradientColors.add(new GradientColor(grossIncomeColor, grossIncomeColor));
        gradientColors.add(new GradientColor(liquidIncomeColor, liquidIncomeColor));
        gradientColors.add(new GradientColor(liquidAmountColor, liquidAmountColor));

        return gradientColors;
    }

    private List<BarEntry> addDataValues(Security security) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, security.getTotalTax(simulateValue.getValue())));
        entries.add(new BarEntry(1, security.getTotalGrossIncome(simulateValue.getValue())));
        entries.add(new BarEntry(2, security.getTotalLiquidIncome(simulateValue.getValue())));
        entries.add(new BarEntry(3, security.getLiquidIncomeTotalAmount(simulateValue.getValue())));
        return entries;
    }

    private void redirectsToBrowserIfUrlIsValid() {
        Uri uri = Uri.parse(observableSecurity.getUrl().getValue());
        Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
        if (validateUrl(webIntent)) {
            startActivity(webIntent);
        }
    }

    private boolean validateUrl(Intent webIntent) {
        Context context = dataBinder.getRoot().getContext();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
        return activities.size() > 0;
    }

    private String parseInterestType(String interestType) {
        if (interestType == "ANO") {
            return getString(R.string.on_expiry);
        } else {
            return interestType;
        }
    }

    private String parseIncomeType(int liquidity, String securityName) {
        if (liquidity == 1) {
            return getString(R.string.daily);
        } else if (securityName.toUpperCase().contains("SEMESTRAL") || securityName.toUpperCase().contains("SEMESTRAIS")) {
            return getString(R.string.semiannual);
        }
        return getString(R.string.on_expiry);
    }
}

