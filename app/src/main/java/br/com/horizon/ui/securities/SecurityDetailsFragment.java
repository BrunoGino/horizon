package br.com.horizon.ui.securities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.model.GradientColor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.SecurityDetailsBinding;
import br.com.horizon.model.Security;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.ui.databinding.ObservableSecurity;
import br.com.horizon.ui.securities.viewmodel.SecurityDetailsViewModel;

public class SecurityDetailsFragment extends Fragment {
    private SecurityDetailsViewModel securityDetailsViewModel;
    private SecurityDetailsBinding dataBinder;
    private String securityId;
    private ObservableSecurity observableSecurity;
    private MutableLiveData<Double> simulateValue;
    private int graphTextColor;


    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        securityDetailsViewModel = ViewModelProviders.of(this).get(SecurityDetailsViewModel.class);
        observableSecurity = new ObservableSecurity();
        simulateValue = new MutableLiveData<>();
        simulateValue.setValue(5000.0);
        securityId = SecurityDetailsFragmentArgs
                .fromBundle(requireArguments())
                .getSecurityId();
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
        graphTextColor = ContextCompat.getColor(view.getContext(), R.color.moccasin);

        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(true, false));

        Security security = createMockSecurity();
        BarChart chart = dataBinder.simulationPlot;

        int taxColor = ContextCompat.getColor(view.getContext(), R.color.graphTax);
        int grossIncomeColor = ContextCompat.getColor(view.getContext(), R.color.graphInterest);
        int liquidIncomeColor = ContextCompat.getColor(view.getContext(), R.color.graphInterestLiq);
        int liquidAmountColor = ContextCompat.getColor(view.getContext(), R.color.graphLiquidIncome);

        List<GradientColor> gradientColors = new ArrayList<>();
        gradientColors.add(new GradientColor(taxColor, taxColor));
        gradientColors.add(new GradientColor(grossIncomeColor, grossIncomeColor));
        gradientColors.add(new GradientColor(liquidIncomeColor, liquidIncomeColor));
        gradientColors.add(new GradientColor(liquidAmountColor, liquidAmountColor));

        chart.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.graphBackground));
        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.getAxisLeft().setTextColor(graphTextColor);
        Legend chartLegend = chart.getLegend();

        List<LegendEntry> legends = new ArrayList<>();
        legends.add(new LegendEntry("Imposto", Legend.LegendForm.CIRCLE, 10f, 10f, null, taxColor));
        legends.add(new LegendEntry("Rend. Bruto", Legend.LegendForm.CIRCLE, 10f, 10f, null, grossIncomeColor));
        legends.add(new LegendEntry("Rend. Líq.", Legend.LegendForm.CIRCLE, 10f, 10f, null, liquidIncomeColor));
        legends.add(new LegendEntry("Montante Líq.", Legend.LegendForm.CIRCLE, 10f, 10f, null, liquidAmountColor));
        chartLegend.setCustom(legends);

        chartLegend.setTextSize(12f);
        chartLegend.setTextColor(graphTextColor);
        chartLegend.setYEntrySpace(5f);
        chartLegend.setFormToTextSpace(5f);

        simulateValue.observe(getViewLifecycleOwner(), aDouble -> {
            BarDataSet barDataSet = new BarDataSet(addDataValues(security, simulateValue.getValue()), "Valor (R$)");
            barDataSet.setGradientColors(gradientColors);
            barDataSet.setValueTextColor(ContextCompat.getColor(view.getContext(), R.color.moccasin));
            barDataSet.setValueTextSize(16f);
            BarData barData = new BarData();
            barData.setValueTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
            barData.addDataSet(barDataSet);
            chart.setData(barData);
            chart.invalidate();
        });

    }

    private List<BarEntry> addDataValues(Security security, Double investedAmount) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, security.getTotalTax(investedAmount)));
        entries.add(new BarEntry(1, security.getTotalIncome(investedAmount)));
        entries.add(new BarEntry(2, security.getLiquidIncome(investedAmount)));
        entries.add(new BarEntry(3, security.getLiquidIncomeAmount(investedAmount)));
        return entries;
    }


    private Security createMockSecurity() {
        return Security.builder().id("1")
                .titleName("Tesouro IPCA+ 2024")
                .titleType("TD")
                .titleValue(5000.0)
                .publisher("Easyinvest")
                .emitter("Tesouro Nacional")
                .interest(2.24)
                .interestType("IPCA")
                .liquidity(1)
                .totalTime(1734)
                .endingDate(new GregorianCalendar(2025, Calendar.AUGUST, 15).getTime())
                .ir(true)
                .fgc(true)
                .url("http://tesouro.fazenda.gov.br/tesouro-direto-precos-e-taxas-dos-titulos")
                .build();
    }

    private void redirectsToBrowserIfUrlIsValid() {
        Uri uri = Uri.parse(observableSecurity.getUrl().getValue());
        Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
        if (validateUrl(webIntent)) {
            startActivity(webIntent);
        }
    }

    private void setupSecurity(ObservableSecurity observableSecurity) {
        securityDetailsViewModel.fetchById(securityId)
                .observe(this, security -> {
                    Log.d("VIEWMODELSEC", "setupSecurity: " + security.toString());
                    observableSecurity.update(security);
                });
    }

    private boolean validateUrl(Intent webIntent) {
        Context context = dataBinder.getRoot().getContext();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
        return activities.size() > 0;
    }


}

