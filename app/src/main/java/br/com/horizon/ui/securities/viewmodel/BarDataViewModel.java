package br.com.horizon.ui.securities.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import br.com.horizon.model.Security;

public class BarDataViewModel extends ViewModel {
    private MutableLiveData<BarDataSet> liveDataSet = new MutableLiveData<>();

    public LiveData<BarDataSet> addDataSet(Security security, Double investedAmount) {
        liveDataSet.postValue(new BarDataSet(addDataValues(security, investedAmount), "Valor (R$)"));
        return liveDataSet;
    }

    private List<BarEntry> addDataValues(Security security, Double investedAmount) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, security.getTotalTax(investedAmount)));
        entries.add(new BarEntry(1, security.getTotalIncome(investedAmount)));
        entries.add(new BarEntry(2, security.getLiquidIncome(investedAmount)));
        entries.add(new BarEntry(3, security.getLiquidIncomeAmount(investedAmount)));
        return entries;
    }

}
