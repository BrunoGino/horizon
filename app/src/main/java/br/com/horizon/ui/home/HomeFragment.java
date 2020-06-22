package br.com.horizon.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.model.GradientColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.HomeFragmentBinding;
import br.com.horizon.ui.BaseFragment;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.viewmodel.HomeViewModel;

public class HomeFragment extends BaseFragment {
    private HomeFragmentBinding homeFragmentBinding;
    private HomeViewModel homeViewModel;
    private NavController controller;
    private BarChart indexesChart;
    private int selicColor;
    private int cdiColor;
    private int igpmColor;
    private int ipcaColor;
    private int graphTextColor;
    private ChartValueFormatter chartValueFormatter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        chartValueFormatter = new ChartValueFormatter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View rootView = homeFragmentBinding.getRoot();
        setupFilterButtonsListeners();
        setupChart(rootView);
        initializeChartDataSet();
        updateChartWithIndexes();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(false, false));
        controller = Navigation.findNavController(view);

    }

    private void updateChartWithIndexes() {
        homeViewModel.observeIndexes(getViewLifecycleOwner(), floats -> {
            List<Float> floatsList = Arrays.asList(floats);
            List<BarEntry> barEntries = floatsList.parallelStream()
                    .map(aFloat -> new BarEntry(floatsList.indexOf(aFloat), aFloat))
                    .collect(Collectors.toList());

            updateChartDataSet(barEntries);
        });
    }

    private void setupChart(View rootView) {
        instantiateChartColors(rootView);
        indexesChart = createBarChart(rootView);
        setupChartLegend(indexesChart);
    }

    private BarChart createBarChart(@NonNull View view) {
        BarChart chart = homeFragmentBinding.homeIndexesPlot;

        chart.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.graphBackground));
        chart.getAxisLeft().setValueFormatter(chartValueFormatter);
        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);


        chart.getAxisLeft().setTextColor(graphTextColor);

        return chart;
    }

    private void initializeChartDataSet() {
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 0.0f));
        barEntries.add(new BarEntry(1, 0.0f));
        barEntries.add(new BarEntry(2, 0.0f));
        barEntries.add(new BarEntry(3, 0.0f));

        BarDataSet barDataSet = createBarDataSet(barEntries);
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        indexesChart.setData(barData);
        indexesChart.invalidate();
    }

    private void updateChartDataSet(List<BarEntry> barEntries) {
        BarDataSet barDataSet = createBarDataSet(barEntries);
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        indexesChart.setData(barData);
        indexesChart.notifyDataSetChanged();
        indexesChart.invalidate();
    }

    private BarDataSet createBarDataSet(List<BarEntry> barEntries) {
        BarDataSet barDataSet = new BarDataSet(barEntries, "");

        barDataSet.setGradientColors(setupBarGradientColors());
        barDataSet.setValueTextColor(graphTextColor);
        barDataSet.setValueFormatter(chartValueFormatter);
        barDataSet.setValueTextSize(16f);
        return barDataSet;
    }

    private List<GradientColor> setupBarGradientColors() {
        List<GradientColor> gradientColors = new ArrayList<>();
        gradientColors.add(new GradientColor(selicColor, selicColor));
        gradientColors.add(new GradientColor(cdiColor, cdiColor));
        gradientColors.add(new GradientColor(ipcaColor, ipcaColor));
        gradientColors.add(new GradientColor(igpmColor, igpmColor));

        return gradientColors;
    }


    private void setupChartLegend(BarChart chart) {
        Legend chartLegend = chart.getLegend();

        List<LegendEntry> legends = new ArrayList<>();
        legends.add(new LegendEntry(getString(R.string.selic), Legend.LegendForm.CIRCLE,
                10f, 10f, null, selicColor));
        legends.add(new LegendEntry(getString(R.string.cdi), Legend.LegendForm.CIRCLE,
                10f, 10f, null, cdiColor));
        legends.add(new LegendEntry(getString(R.string.ipca), Legend.LegendForm.CIRCLE,
                10f, 10f, null, ipcaColor));
        legends.add(new LegendEntry(getString(R.string.igpm), Legend.LegendForm.CIRCLE,
                10f, 10f, null, igpmColor));
        chartLegend.setCustom(legends);

        chartLegend.setTextSize(12f);
        chartLegend.setTextColor(graphTextColor);
        chartLegend.setYEntrySpace(5f);
        chartLegend.setFormToTextSpace(5f);
    }

    private void instantiateChartColors(View view) {
        graphTextColor = ContextCompat.getColor(view.getContext(), R.color.white);
        selicColor = ContextCompat.getColor(view.getContext(), R.color.graphTax);
        cdiColor = ContextCompat.getColor(view.getContext(), R.color.graphInterest);
        ipcaColor = ContextCompat.getColor(view.getContext(), R.color.graphLiquidIncome);
        igpmColor = ContextCompat.getColor(view.getContext(), R.color.graphInterestLiq);
    }

    private void setupFilterButtonsListeners() {
        setByTypeFilterListeners();
        setByInterestOrderFilterListeners();
        setByFavoritesFilterListeners();
    }

    private void setByFavoritesFilterListeners() {
        homeFragmentBinding.homeMostFavoritesIcon.setOnClickListener(v -> goesToMostFavoritedList());
        homeFragmentBinding.homeMostFavoritesLabel.setOnClickListener(v -> goesToMostFavoritedList());
    }

    private void setByInterestOrderFilterListeners() {
        homeFragmentBinding.homeGreaterInterestsIcon.setOnClickListener(v -> goesToListOrderedByInterest());
        homeFragmentBinding.homeGreaterInterestsLabel.setOnClickListener(v -> goesToListOrderedByInterest());
    }

    private void setByTypeFilterListeners() {
        homeFragmentBinding.homeTdFilterButton.setOnClickListener(v -> goesToListFilteredByType("TD"));
        homeFragmentBinding.homeCdbFilterButton.setOnClickListener(v -> {
            String titleType = String.valueOf(homeFragmentBinding.homeCdbFilterButton.getText());
            goesToListFilteredByType(titleType);
        });
        homeFragmentBinding.homeLciLcaFilterButton.setOnClickListener(v -> {
            String titleType = String.valueOf(homeFragmentBinding.homeLciLcaFilterButton.getText());
            goesToListFilteredByType(titleType);
        });
        homeFragmentBinding.homeCriCraFilterButton.setOnClickListener(v -> {
            String titleType = String.valueOf(homeFragmentBinding.homeCriCraFilterButton.getText());
            goesToListFilteredByType(titleType);
        });
        homeFragmentBinding.homeDebFilterButton.setOnClickListener(v -> {
            String titleType = String.valueOf(homeFragmentBinding.homeDebFilterButton.getText());
            goesToListFilteredByType(titleType);
        });
    }

    private void goesToListOrderedByInterest() {
        NavDirections direction = HomeFragmentDirections
                .actionHomeFragmentToSecuritiesListFragment("", true, false);
        controller.navigate(direction);
    }

    private void goesToMostFavoritedList() {
        NavDirections direction = HomeFragmentDirections
                .actionHomeFragmentToSecuritiesListFragment("", false, true);
        controller.navigate(direction);
    }

    private void goesToListFilteredByType(String titleType) {
        NavDirections direction = HomeFragmentDirections
                .actionHomeFragmentToSecuritiesListFragment(titleType, false, false);

        controller.navigate(direction);
    }
}
