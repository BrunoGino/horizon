package br.com.horizon.ui.indexes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.horizon.R;
import br.com.horizon.databinding.FragmentIndexesBinding;
import br.com.horizon.model.Index;
import br.com.horizon.repository.resource.Resource;
import br.com.horizon.ui.BaseFragment;
import br.com.horizon.ui.indexes.chart.ChartValueFormatter;
import br.com.horizon.ui.indexes.chart.DateAxisValueFormatter;
import br.com.horizon.viewmodel.HomeViewModel;
import br.com.horizon.viewmodel.IndexesViewModel;

public class IndexesFragment extends BaseFragment {
    private FragmentIndexesBinding viewBinder;
    private IndexesViewModel indexesViewModel;
    private LineChart indexesChart;
    private int selicColor;
    private int cdiColor;
    private int igpmColor;
    private int ipcaColor;
    private int graphTextColor;
    private ChartValueFormatter chartValueFormatter;
    private List<ILineDataSet> dataSets;
    private DateAxisValueFormatter dateValueFormatter;
    private List<Entry> selicEntries;
    private List<Entry> ipcaEntries;
    private List<Entry> cdiEntries;
    private List<Entry> igpmEntries;
    private LineDataSet selicDataSet;
    private LineDataSet cdiDataSet;
    private LineDataSet ipcaDataSet;
    private LineDataSet igpmDataSet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        indexesViewModel = ViewModelProviders.of(this).get(IndexesViewModel.class);
        chartValueFormatter = new ChartValueFormatter();
        dateValueFormatter = new DateAxisValueFormatter();
        dataSets = new ArrayList<>();
        selicEntries = initializeEmptyEntries();
        ipcaEntries = initializeEmptyEntries();
        cdiEntries = initializeEmptyEntries();
        igpmEntries = initializeEmptyEntries();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinder = FragmentIndexesBinding.inflate(inflater, container, false);
        View root = viewBinder.getRoot();
        instantiateChartColors(root);
        setupChart(root);
        selicDataSet = buildLineDataSet(selicEntries, "Selic", selicColor);
        cdiDataSet = buildLineDataSet(cdiEntries, "CDI", cdiColor);
        ipcaDataSet = buildLineDataSet(ipcaEntries, "IPCA", ipcaColor);
        igpmDataSet = buildLineDataSet(igpmEntries, "IGP-M", igpmColor);
        initializeChartDataSet();
        updateChartWithIndexes();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.indexes_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_indexes_refresh) {
            updateChartWithIndexes();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupChart(View rootView) {
        indexesChart = createLineChart(rootView);
        setupChartLegend(indexesChart);
    }

    private LineChart createLineChart(@NonNull View view) {
        LineChart chart = viewBinder.indexesPlot;
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

    private void initializeChartDataSet() {
        dataSets.add(selicDataSet);
        dataSets.add(cdiDataSet);
        dataSets.add(ipcaDataSet);
        dataSets.add(igpmDataSet);
    }

    private void updateChartWithIndexes() {
        indexesViewModel.getIPCALiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getData() != null) {
                updateChartWithEntries(resource, ipcaDataSet);
            }
        });

        indexesViewModel.getSelicLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getData() != null) {
                updateChartWithEntries(resource, selicDataSet);
            }
        });

        indexesViewModel.getIGPMLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getData() != null) {
                updateChartWithEntries(resource, igpmDataSet);
            }
        });
        indexesViewModel.getCDILiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getData() != null) {
                updateChartWithEntries(resource, cdiDataSet);
            }
        });
    }

    private void updateChartWithEntries(Resource<List<Index>> resource, LineDataSet lineDataSet) {
        List<Entry> entries = Objects.requireNonNull(resource.getData()).stream().map(index ->
                new Entry(index.getIndexDate(), index.getValue())).collect(Collectors.toList());
        lineDataSet.setValues(entries);
        LineData lineData = new LineData(dataSets);
        indexesChart.setData(lineData);
        if (indexesChart.getData().getDataSetCount() >= 3) {
            indexesChart.notifyDataSetChanged();
            indexesChart.animateXY(3000, 3000);
            indexesChart.invalidate();
        }
    }

    private LineDataSet buildLineDataSet(List<Entry> values, String label, int lineColor) {
        LineDataSet lineDataSet = new LineDataSet(values, label);
        lineDataSet.setColor(lineColor);
        lineDataSet.setCircleColor(lineColor);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(11);
        lineDataSet.setValueFormatter(chartValueFormatter);
        lineDataSet.setValueTextColor(graphTextColor);

        return lineDataSet;
    }

    private List<Entry> initializeEmptyEntries() {
        return new ArrayList<>();
    }
}
