package br.com.horizon.ui.databinding;

import androidx.lifecycle.MutableLiveData;

import java.util.Date;

import br.com.horizon.model.Security;
import lombok.Getter;

public class ObservableSecurity {
    private Security security;
    @Getter
    private final MutableLiveData<String> titleName = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Double> interest = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<String> interestType = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<String> emitter = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Date> endingDate = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Boolean> fgc = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Boolean> ir = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Integer> liquidity = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<String> publisher = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<String> titleType = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Double> titleValue = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Integer> totalTime = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<String> url = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Float> totalIrTaxPercentage = new MutableLiveData<>();


    public ObservableSecurity() {

        this.security = Security.builder().id("1")
                .titleName("Tesouro IPCA+ 2024")
                .titleType("TD")
                .titleValue(5000.0)
                .publisher("Tesouro Direto")
                .emitter("Tesouro Nacional")
                .interest(2.24)
                .interestType("IPCA")
                .liquidity(1)
                .totalTime(1734)
                .endingDate(new Date(2024,02,14))
                .ir(false)
                .fgc(false)
                .url("http://tesouro.fazenda.gov.br/tesouro-direto-precos-e-taxas-dos-titulos")
                .build();

        this.titleName.setValue(security.getTitleName());
        this.interest.setValue(security.getInterest());
        this.interestType.setValue(security.getInterestType());
        this.emitter.setValue(security.getEmitter());
        this.fgc.setValue(security.getFgc());
        this.ir.setValue(security.getIr());
        this.liquidity.setValue(security.getLiquidity());
        this.publisher.setValue(security.getPublisher());
        this.titleType.setValue(security.getTitleType());
        this.titleValue.setValue(security.getTitleValue());
        this.totalTime.setValue(security.getTotalTime());
        this.url.setValue(security.getUrl());
        this.totalIrTaxPercentage.setValue(security.getTaxPercentage());
    }

    public void update(Security security) {
        this.security = security;
        titleName.postValue(security.getTitleName());
        titleType.postValue(security.getTitleType());
        titleValue.postValue(security.getTitleValue());
        interest.postValue(security.getInterest());
        interestType.postValue(security.getInterestType());
        liquidity.postValue(security.getLiquidity());
        emitter.postValue(security.getEmitter());
        publisher.postValue(security.getPublisher());
        fgc.postValue(security.getFgc());
        ir.postValue(security.getIr());
        totalTime.postValue(security.getTotalTime());
        url.postValue(security.getUrl());
        totalIrTaxPercentage.postValue(security.getTaxPercentage());
    }

    public Security toSecurity() {
        return Security.builder()
                .titleName(titleName.getValue())
                .titleType(titleType.getValue())
                .titleValue(titleValue.getValue())
                .interest(interest.getValue())
                .interestType(interestType.getValue())
                .liquidity(liquidity.getValue())
                .emitter(emitter.getValue())
                .publisher(publisher.getValue())
                .fgc(fgc.getValue())
                .ir(ir.getValue())
                .totalTime(totalTime.getValue())
                .url(url.getValue())
                .build();
    }

}
