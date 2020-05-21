package br.com.horizon.ui.securities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.model.GradientColor;

import java.util.ArrayList;
import java.util.List;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.SecurityDetailsBinding;
import br.com.horizon.model.Security;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.ui.databinding.ObservableSecurity;

public class SecurityDetailsFragment extends Fragment {
    private SecurityDetailsBinding dataBinder;
    private ObservableSecurity observableSecurity;
    private MutableLiveData<Double> simulateValue;
    private int graphTextColor;
    private int taxColor;
    private int grossIncomeColor;
    private int liquidIncomeColor;
    private int liquidAmountColor;


    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observableSecurity = new ObservableSecurity();
        simulateValue = new MutableLiveData<>();
        simulateValue.setValue(5000.0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinder = SecurityDetailsBinding.inflate(inflater, container, false);
        dataBinder.setLifecycleOwner(this);
        dataBinder.setSecurity(observableSecurity);
        dataBinder.setUrlClick(v -> redirectsToBrowserIfUrlIsValid());
        dataBinder.setSimulateValue(simulateValue);
        return dataBinder.getRoot();
    }

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(true, false));
        instantiateChartColors(view);
        BarChart chart = createBarChart(view);
        setupChartLegend(chart);

        Security security = observableSecurity.toSecurity();

        dataBinder.securityDetailAnnualGrossIncomeValue.setText(
                String.valueOf(security.getGrossAnnualIncome(simulateValue.getValue())));
        dataBinder.securityDetailAnnualLiquidIncomeValue.setText(
                String.valueOf(security.getLiquidAnnualIncome(simulateValue.getValue())));

        simulateValue.observe(getViewLifecycleOwner(), aDouble -> {
            if (simulateValue.getValue() >= observableSecurity.getTitleValue().getValue()) {
                updateChartDataSet(observableSecurity.toSecurity(), chart);
                dataBinder.securityDetailSimulateValue.setErrorEnabled(false);
            } else {
                dataBinder.securityDetailSimulateValueText.requestFocus();
                dataBinder.securityDetailSimulateValue.setErrorEnabled(true);
                dataBinder.securityDetailSimulateValue.setError(getString(R.string.insertAValueGreaterOrEqualTo)
                        + " " + getString(R.string.currency_symbol) + observableSecurity.getTitleValue().getValue());
            }

        });
    }

    private void updateChartDataSet(Security security, BarChart chart) {
        BarDataSet barDataSet = new BarDataSet(addDataValues(security, simulateValue.getValue()), "");
        barDataSet.setGradientColors(setupBarColors());
        barDataSet.setValueTextColor(graphTextColor);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        chart.setData(barData);
        chart.invalidate();
    }

    private void instantiateChartColors(@NonNull View view) {
        graphTextColor = ContextCompat.getColor(view.getContext(), R.color.moccasin);
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

    private List<BarEntry> addDataValues(Security security, Double investedAmount) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, security.getTotalTax(investedAmount)));
        entries.add(new BarEntry(1, security.getTotalIncome(investedAmount)));
        entries.add(new BarEntry(2, security.getLiquidIncome(investedAmount)));
        entries.add(new BarEntry(3, security.getLiquidIncomeAmount(investedAmount)));
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


}

