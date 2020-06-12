package br.com.horizon.model;

import android.text.Editable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Filter {
    private String emitter;
    private String publisher;
    private String incomeType;
    private String liquidity;
    private Double irValue;
    private Double interestMax;
    private Double interestMin;

    public void setEndingDate(Editable text) {

    }

    public void setFgc(boolean checked) {

    }
}
