package br.com.horizon.ui.home;

import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.HomeFragmentBinding;
import br.com.horizon.model.Index;
import br.com.horizon.repository.resource.Resource;
import br.com.horizon.ui.BaseFragment;
import br.com.horizon.ui.VisualComponents;
import br.com.horizon.ui.home.chart.ChartValueFormatter;
import br.com.horizon.ui.home.chart.DateAxisValueFormatter;
import br.com.horizon.ui.home.recyclerview.TypeCardAdapter;
import br.com.horizon.viewmodel.HomeViewModel;

public class HomeFragment extends BaseFragment {
    private HomeFragmentBinding homeFragmentBinding;
    private HomeViewModel homeViewModel;
    private NavController controller;
    private LineChart indexesChart;
    private int selicColor;
    private int cdiColor;
    private int igpmColor;
    private int ipcaColor;
    private int graphTextColor;
    private ChartValueFormatter chartValueFormatter;
    private List<ILineDataSet> dataSets;
    private TypeCardAdapter typeCardAdapter;
    private DateAxisValueFormatter dateValueFormatter;
    private List<Entry> selicEntries;
    private List<Entry> ipcaEntries;
    private List<Entry> cdiEntries;
    private List<Entry> igpmEntries;
    private LineDataSet selicDataSet;
    private LineDataSet cdiDataSet;
    private LineDataSet ipcaDataSet;
    private LineDataSet igpmDataSet;
    private List<String> titleTypes;
    private TypeCardAdapter.OnItemClickListener onRecyclerItemClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        chartValueFormatter = new ChartValueFormatter();
        dateValueFormatter = new DateAxisValueFormatter();
        dataSets = new ArrayList<>();
        selicEntries = initializeEmptyEntries();
        ipcaEntries = initializeEmptyEntries();
        cdiEntries = initializeEmptyEntries();
        igpmEntries = initializeEmptyEntries();
        titleTypes = new ArrayList<>(Arrays.asList(getString(R.string.public_titles),
                getString(R.string.cdb), getString(R.string.lci_lca), getString(R.string.cri_cra),
                getString(R.string.debentures), getString(R.string.all_titles)));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View rootView = homeFragmentBinding.getRoot();
        setupFilterButtonsListeners();
        instantiateChartColors(rootView);
        setupChart(rootView);
        selicDataSet = buildLineDataSet(selicEntries, "Selic", selicColor);
        cdiDataSet = buildLineDataSet(cdiEntries, "CDI", cdiColor);
        ipcaDataSet = buildLineDataSet(ipcaEntries, "IPCA", ipcaColor);
        igpmDataSet = buildLineDataSet(igpmEntries, "IGP-M", igpmColor);
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
        setupRecyclerView(view);
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = homeFragmentBinding.titleTypesRecycler;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        setOnRecyclerItemClickListener();
        typeCardAdapter = new TypeCardAdapter(view.getContext(), this.onRecyclerItemClickListener);
        typeCardAdapter.setHasStableIds(true);
        recyclerView.setAdapter(typeCardAdapter);
        typeCardAdapter.addAll(titleTypes);
        typeCardAdapter.notifyDataSetChanged();
    }

    private void setOnRecyclerItemClickListener() {
        onRecyclerItemClickListener = securityType -> {
            if (securityType.equals("Tesouro Direto")) {
                goesToListFilteredByType("TD");
            } else if (securityType.equals("Todos os títulos")) {
                Log.d("ALLTITLES", "goesToAllSecuritiesList");
                goesToAllSecuritiesList();
            } else {
                goesToListFilteredByType(securityType);
            }
        };
    }

    private void goesToAllSecuritiesList() {
        NavDirections direction = HomeFragmentDirections
                .actionHomeFragmentToSecuritiesListFragment("all", false, false);

        controller.navigate(direction);
    }

    private void updateChartWithIndexes() {
        homeViewModel.getIPCALiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getData() != null) {
                updateChartWithEntries(resource, ipcaDataSet);
            }
        });

        homeViewModel.getSelicLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getData() != null) {
                updateChartWithEntries(resource, selicDataSet);
            }
        });

        homeViewModel.getIGPMLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getData() != null) {
                updateChartWithEntries(resource, igpmDataSet);
            }
        });
        homeViewModel.getCDILiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getData() != null) {
                updateChartWithEntries(resource, cdiDataSet);
            }
        });

    }

    private void updateChartWithEntries(Resource<List<Index>> resource, LineDataSet lineDataSet) {
        List<Entry> entries = resource.getData().stream().map(index ->
                new Entry(index.getIndexDate(), index.getValue())).collect(Collectors.toList());
        lineDataSet.setValues(entries);
        LineData lineData = new LineData(dataSets);
        indexesChart.setData(lineData);
        indexesChart.notifyDataSetChanged();
        indexesChart.animateXY(3000, 3000);
        indexesChart.invalidate();
    }

    private LineDataSet buildLineDataSet(List<Entry> values, String label, int lineColor) {
        LineDataSet lineDataSet = new LineDataSet(values, label);
        lineDataSet.setColor(lineColor);
        lineDataSet.setCircleColor(lineColor);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueFormatter(chartValueFormatter);
        lineDataSet.setValueTextColor(graphTextColor);

        return lineDataSet;
    }

    private void setupChart(View rootView) {
        indexesChart = createLineChart(rootView);
        setupChartLegend(indexesChart);
    }

    private LineChart createLineChart(@NonNull View view) {
        LineChart chart = homeFragmentBinding.homeIndexesPlot;
        XAxis xAxis = chart.getXAxis();
        YAxis axisLeft = chart.getAxisLeft();
        YAxis axisRight = chart.getAxisRight();

        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
        chart.setNoDataText(getString(R.string.loading_indexes));

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(view.getContext(), R.color.graphAxisLabelColor));
        xAxis.setValueFormatter(dateValueFormatter);

        axisLeft.setTextColor(ContextCompat.getColor(view.getContext(), R.color.graphAxisLabelColor));
        axisLeft.setValueFormatter(chartValueFormatter);
        axisLeft.setYOffset(20);
        axisLeft.setSpaceMin(20);

        axisRight.setEnabled(false);

        return chart;
    }

    private void initializeChartDataSet() {
        dataSets.add(selicDataSet);
        dataSets.add(cdiDataSet);
        dataSets.add(ipcaDataSet);
        dataSets.add(igpmDataSet);
//        indexesChart.setData(new LineData(dataSets));
//        indexesChart.notifyDataSetChanged();
//        indexesChart.invalidate();
    }

    private List<Entry> initializeEmptyEntries() {
        return new ArrayList<>();
//                new ArrayList<>(Arrays.asList(new Entry(0, 0.0f), new Entry(0, 0.0f), new Entry(0, 0.0f), new Entry(0, 0.0f)));
    }

    private void setupChartLegend(LineChart chart) {
        Legend chartLegend = chart.getLegend();

        List<LegendEntry> legends = new ArrayList<>();
        legends.add(new LegendEntry(getString(R.string.selic), Legend.LegendForm.LINE,
                20f, 10f, null, selicColor));
        legends.add(new LegendEntry(getString(R.string.cdi), Legend.LegendForm.LINE,
                20f, 10f, null, cdiColor));
        legends.add(new LegendEntry(getString(R.string.ipca), Legend.LegendForm.LINE,
                20f, 10f, null, ipcaColor));
        legends.add(new LegendEntry(getString(R.string.igpm), Legend.LegendForm.LINE,
                20f, 10f, null, igpmColor));

        chartLegend.setCustom(legends);
        chartLegend.setTextSize(15f);
        chartLegend.setTextColor(graphTextColor);
        chartLegend.setYEntrySpace(5f);
        chartLegend.setXEntrySpace(15f);
        chartLegend.setFormToTextSpace(5f);
    }

    private void instantiateChartColors(View view) {
        graphTextColor = ContextCompat.getColor(view.getContext(), R.color.white);
        selicColor = ContextCompat.getColor(view.getContext(), R.color.graphSelic);
        cdiColor = ContextCompat.getColor(view.getContext(), R.color.graphCdi);
        ipcaColor = ContextCompat.getColor(view.getContext(), R.color.graphIpca);
        igpmColor = ContextCompat.getColor(view.getContext(), R.color.graphIgpm);
    }

    private void setupFilterButtonsListeners() {
        setByTypeFilterListeners();
    }

    private void setByTypeFilterListeners() {
//        homeFragmentBinding.homeTdFilterButton.setOnClickListener(v -> goesToListFilteredByType("TD"));
//        homeFragmentBinding.homeCdbFilterButton.setOnClickListener(v -> {
//            String titleType = String.valueOf(homeFragmentBinding.homeCdbFilterButton.getText());
//            goesToListFilteredByType(titleType);
//        });
//        homeFragmentBinding.homeLciLcaFilterButton.setOnClickListener(v -> {
//            String titleType = String.valueOf(homeFragmentBinding.homeLciLcaFilterButton.getText());
//            goesToListFilteredByType(titleType);
//        });
//        homeFragmentBinding.homeCriCraFilterButton.setOnClickListener(v -> {
//            String titleType = String.valueOf(homeFragmentBinding.homeCriCraFilterButton.getText());
//            goesToListFilteredByType(titleType);
//        });
//        homeFragmentBinding.homeDebFilterButton.setOnClickListener(v -> {
//            String titleType = String.valueOf(homeFragmentBinding.homeDebFilterButton.getText());
//            goesToListFilteredByType(titleType);
//        });
    }

    private void goesToListFilteredByType(String titleType) {
        NavDirections direction = HomeFragmentDirections
                .actionHomeFragmentToSecuritiesListFragment(titleType, false, false);

        controller.navigate(direction);
    }
}

