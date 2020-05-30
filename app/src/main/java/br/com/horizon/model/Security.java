package br.com.horizon.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Security {
    private String id;
    private String titleName;
    private Double interest;
    private String interestType;
    private String emitter;
    private Date endingDate;
    private Boolean fgc;
    private Boolean ir;
    private Integer liquidity;
    private String publisher;
    private String titleType;
    private Double titleValue;
    private Integer totalTime;
    private String url;

    public float getTotalTax(double simulateValue) {
        if (ir) {
            return getGrossAnnualIncome(simulateValue) * getTaxPercentage();
        }
        return 0;
    }

    public float getTaxPercentage() {
        if (totalTime >= 181 && totalTime <= 360) {
            return 0.20f;
        } else if (totalTime >= 361 && totalTime <= 720) {
            return 0.175f;
        } else {
            return 0.15f;
        }
    }

    public float getTotalGrossIncome(double simulateValue) {
        double totalIncomeTax = getTotalTimeInYears() * (interest / 100);
        return (float) (totalIncomeTax * simulateValue);
    }

    public float getTotalLiquidIncome(double simulateValue) {
        return getTotalGrossIncome(simulateValue) - getTotalTax(simulateValue);
    }

    public float getLiquidIncomeTotalAmount(double simulateValue) {
        return (float) (simulateValue
                + getTotalLiquidIncome(simulateValue));
    }

    private float getTotalTimeInYears() {
        return totalTime / 365f;
    }

    public float getLiquidAnnualIncome(double simulateValue) {
        return getGrossAnnualIncome(simulateValue) - (getTaxPercentage() / getTotalTimeInYears());
    }

    public Double getLiquidAnnualInterest(double simulateValue) {
        return interest - ((getAnnualIrTaxValue(simulateValue) * interest)
                / getGrossAnnualIncome(simulateValue));
    }

    private float getAnnualIrTaxValue(double simulateValue) {
        return getTaxPercentage() * getGrossAnnualIncome(simulateValue);
    }

    public float getGrossAnnualIncome(double simulateValue) {
        return (float) (simulateValue * (interest / 100.0));
    }
}
