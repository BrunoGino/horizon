package br.com.horizon;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import br.com.horizon.model.Security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SecurityTest {
    private Security security;
    private Calendar calendar;
    private Date date;

    @Before
    public void setup() {
//        this.calendar = Calendar.getInstance();
//        this.date = new Date();
//
//        this.security = Security.builder()
//                .emitter("Itausa")
//                .publisher("Modal")
//                .endingDate(date)
//                .fgc(true)
//                .ir(true)
//                .interest(2.5)
//                .interestType("IPCA")
//                .liquidity(1)
//                .titleName("CDB Itausa IPCA + 2.5")
//                .titleType("CDB")
//                .titleValue(5000.0)
//                .url("www.google.com/")
//                .id("1")
//                .build();
    }

    @Test
    public void must_ReturnLiquidValue_WhenIRIsTrueAndEndingDateIsTill180Days() {
//        calendar.set(2020,Calendar.DECEMBER,9);
//        date = calendar.getTime();
//
//        assertThat(security.getTitleValue(), equalTo(5112.375));
    }
}
