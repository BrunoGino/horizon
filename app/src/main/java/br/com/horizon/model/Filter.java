package br.com.horizon.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Filter {
    private String emitter;
    private String publisher;
    private String incomeType;
    private Integer liquidity;
    private Double ir;
    private Double interestMax;
    private Double interestMin;
}
