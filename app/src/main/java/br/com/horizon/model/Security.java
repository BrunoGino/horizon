package br.com.horizon.model;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Security {
    private String id;
    private String titleName;
    private Double interest;
    private String interestType;
    private String emitter;
    private Date endingDate;
    private Boolean ir;
    private Integer liquidity;
    private String publisher;
    private String titleType;
    private Double titleValue;
    private Integer totalTime;
    private String url;


}
