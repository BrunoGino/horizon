package br.com.horizon.model;

import android.text.Editable;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Filter implements Serializable {
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
