package br.com.horizon.ui.securities.chart;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;

public class LeftAxisFormatter extends ValueFormatter {
    private NumberFormat currencyFormatter;

    public LeftAxisFormatter() {
        currencyFormatter = NumberFormat.getCurrencyInstance();
        currencyFormatter.setMinimumFractionDigits(2);
    }

    @Override
    public String getFormattedValue(float value) {
        return currencyFormatter.format(value);
    }
}
