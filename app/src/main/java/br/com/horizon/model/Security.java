package br.com.horizon.model;

import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
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


}
