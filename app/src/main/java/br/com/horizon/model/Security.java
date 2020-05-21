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

    public float getTotalTax(double investedAmount) {
        return getTotalIncome(investedAmount) * getTaxPercentage();
    }

    private float getTaxPercentage() {
        if (totalTime >= 181 && totalTime <= 360) {
            return 0.20f;
        } else if (totalTime >= 361 && totalTime <= 720) {
            return 0.175f;
        } else {
            return 0.15f;
        }
    }

    public float getTotalIncome(double investedAmount) {
        double totalIncomeTax = getTotalTimeInYears() * (interest / 100);
        return Float.parseFloat(String.valueOf(totalIncomeTax * investedAmount));
    }

    public float getLiquidIncome(double investedAmount) {
        return Float.parseFloat(String.valueOf(getTotalIncome(investedAmount) - getTotalTax(investedAmount)));
    }

    public float getLiquidIncomeAmount(double investedAmount) {
        return Float.parseFloat(String.valueOf(investedAmount
                + getLiquidIncome(investedAmount)));
    }

    public float getLiquidAnnualIncome(double investedAmount) {
        return (float) getGrossAnnualIncome(investedAmount) - (getTaxPercentage() / getTotalTimeInYears());
    }

    public float getGrossAnnualIncome(double investedAmount) {
        return (float) (investedAmount * (interest));
    }


    private float getTotalTimeInYears() {
        return totalTime / 365f;
    }


}
