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
import androidx.lifecycle.ViewModelProviders;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabeler;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.model.GradientColor;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.SecurityDetailsBinding;
import br.com.horizon.model.Security;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.ui.databinding.ObservableSecurity;

public class SecurityDetailsFragment extends Fragment {
    private SecurityDetailsViewModel viewModel;
    private SecurityDetailsBinding dataBinder;
    private String securityId;
    private ObservableSecurity observableSecurity;


    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SecurityDetailsViewModel.class);
        observableSecurity = new ObservableSecurity();
        securityId = SecurityDetailsFragmentArgs
                .fromBundle(Objects.requireNonNull(getArguments()))
                .getSecurityId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinder = SecurityDetailsBinding.inflate(inflater, container, false);
        dataBinder.setLifecycleOwner(this);
        dataBinder.setSecurity(observableSecurity);
        dataBinder.setUrlClick(v -> redirectsToBrowserIfUrlIsValid());
        return dataBinder.getRoot();
    }

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(true, false));

        Security security = createMockSecurity();

        BarChart chart = view.findViewById(R.id.simulation_plot);

        BarDataSet barDataSet = new BarDataSet(dataValues(security), "Valor (R$)");

        int taxStartColor = ContextCompat.getColor(view.getContext(), R.color.graphTaxLight);
        int taxEndColor = ContextCompat.getColor(view.getContext(),R.color.graphTax);
        int incomeStartColor = ContextCompat.getColor(view.getContext(),R.color.graphInterestLight);
        int incomeEndColor = ContextCompat.getColor(view.getContext(),R.color.graphInterest);
        int liquidIncomeStartColor = ContextCompat.getColor(view.getContext(), R.color.graphInterestLiqLight);
        int liquidIncomeEndColor = ContextCompat.getColor(view.getContext(), R.color.graphInterestLiq);
        int liquidAmountStart = ContextCompat.getColor(view.getContext(), R.color.graphLiquidIncomeLight);
        int liquidAmountEnd = ContextCompat.getColor(view.getContext(), R.color.graphLiquidIncome);

        List<GradientColor>  gradientColors = new ArrayList<>();
        gradientColors.add(new GradientColor(taxStartColor,taxEndColor));
        gradientColors.add(new GradientColor(incomeStartColor,incomeEndColor));
        gradientColors.add(new GradientColor(liquidIncomeStartColor,liquidIncomeEndColor));
        gradientColors.add(new GradientColor(liquidAmountStart,liquidAmountEnd));

        barDataSet.setGradientColors(gradientColors);

        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barData.setValueTextSize(16f);
        barData.setValueTextColor(ContextCompat.getColor(view.getContext(), R.color.white));

        chart.setData(barData);
        chart.invalidate();

    }

    private ArrayList<BarEntry> dataValues(Security security) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, security.getTotalTax(security.getTitleValue())));
        entries.add(new BarEntry(1, security.getTotalIncome(security.getTitleValue())));
        entries.add(new BarEntry(2, security.getLiquidIncome(security.getTitleValue())));
        entries.add(new BarEntry(3, security.getLiquidIncomeAmount(security.getTitleValue())));
        return entries;
    }

    private LineAndPointFormatter formatSeries(LineAndPointFormatter series1Format) {
        series1Format.setPointLabeler(new PointLabeler() {
            DecimalFormat df = new DecimalFormat("###.###");

            @Override
            public String getLabel(XYSeries series, int index) {
                return df.format(series.getY(index));
            }
        });
        return series1Format;
    }

    private void stylizeXAxis(XYPlot plot, List<Number> domainLabels) {
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels.get(i));
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });
    }

    private List<Entry> generateDomainLabels(long totalMonths) {
        double semester = totalMonths / 6;
        List<Entry> months = new ArrayList<>();
        for (int i = 0; i <= totalMonths; i += semester) {
            months.add(new Entry(i, i));
        }
        return months;
    }

    private List<Entry> generateTaxLabels(long months, double interestTax, double investedAmount) {
        List<Entry> taxes = new ArrayList<>();
        if ((months * 30.417) > 720) {
            long years = months / 12;
            double semesterTax = (0.15 / years) / 2;
            long semesters = months / 6;
            double totalInterest = 0;
            for (int i = 0; i < months; i += semesters) {
                totalInterest += (interestTax / 20) * investedAmount;
                float tax = Float.parseFloat(String.valueOf(semesterTax * totalInterest));
                taxes.add(new Entry(tax, tax));
            }
        }
        return taxes;
    }

    private List<Entry> generateIncomeLabels(long months, double interestTax, double investedAmount) {
        List<Entry> incomes = new ArrayList<>();
        long semesters = months / 6;
        float totalInterest = 0;

        for (int i = 0; i < months; i += semesters) {
            totalInterest += (interestTax / 200) * investedAmount;
            incomes.add(new Entry(totalInterest, totalInterest));
        }

        return incomes;
    }

    private Security createMockSecurity() {
        return Security.builder().id("1")
                .titleName("Tesouro IPCA+ 2024")
                .titleType("TD")
                .titleValue(5000.0)
                .publisher("")
                .emitter("")
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
        viewModel.fetchById(securityId)
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

