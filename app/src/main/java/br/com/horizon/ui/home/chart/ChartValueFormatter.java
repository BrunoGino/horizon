package br.com.horizon.ui.home.chart;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;

public class ChartValueFormatter extends ValueFormatter {
    private NumberFormat percentageFormatter;

    public ChartValueFormatter() {
        percentageFormatter = NumberFormat.getPercentInstance();
        percentageFormatter.setMinimumFractionDigits(2);
    }

    @Override
    public String getFormattedValue(float value) {
        return percentageFormatter.format(value);
    }
}
