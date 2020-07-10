package br.com.horizon.ui.indexes.chart;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAxisValueFormatter extends ValueFormatter {
    private DateFormat dateTimeFormatter;

    public DateAxisValueFormatter() {
        dateTimeFormatter = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
    }

    @Override
    public String getFormattedValue(float value) {
        long convertedTimestamp = (long) value;
        Date date = new Date();
        date.setTime(convertedTimestamp);
        return dateTimeFormatter.format(date);
    }
}
