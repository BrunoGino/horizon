package br.com.horizon.ui.converter;

import androidx.databinding.InverseMethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Converter {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy", Locale.getDefault());

    @InverseMethod("convertIntegerToString")
    public static Integer convertStringToInteger(String value) {
        try {
            return Integer.parseInt(Objects.requireNonNull(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @InverseMethod("convertDateToString")
    public static Date convertStringToDate(String value) {
        try {
            return dateFormat.parse(Objects.requireNonNull(value));
        } catch (ParseException e) {
            return new Date();
        }
    }

    @InverseMethod("convertDoubleToString")
    public static Double convertStringToDouble(String value) {
        return Double.parseDouble(Objects.requireNonNull(value));
    }

    @InverseMethod("convertBooleanToString")
    public static Boolean convertStringToBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    public static String convertDoubleToString(Double value) {
        return String.valueOf(value);
    }

    public static String convertBooleanToString(Boolean value) {
        return String.valueOf(value);
    }

    public static String convertIntegerToString(Integer value) {
        return String.valueOf(value);
    }

    public static String convertDateToString(Date date) {
        try {
            return dateFormat.format(date);
        } catch (NullPointerException e) {
            return dateFormat.format(new Date());
        }
    }
}
