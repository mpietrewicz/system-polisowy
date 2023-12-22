package pl.mpietrewicz.sp.modules.balance.domain.balance;

import org.junit.Test;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy.WITHOUT_LIMITS;

public class TestBalance {

    @Test
    public void newTest() {
        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, BigDecimal.TEN, contractData.getAggregateId());
        balance.addPayment(date("2023-02-10"), amount("30"), WITHOUT_LIMITS);
        balance.addRefund(date("2023-03-10"), amount("20"));
        balance.addPayment(date("2023-05-20"), amount("60"), WITHOUT_LIMITS);
        balance.changePremium(LocalDate.parse("2023-02-01"), new BigDecimal(100), contractData.getAggregateId());

        System.out.println("koniec");
    }

    @Test
    public void testProductionExample() {
        LocalDate contractStart = LocalDate.parse("2022-03-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, new BigDecimal("11.78"), contractData.getAggregateId());
        balance.addPayment(date("2022-02-25"), amount("35.34"), WITHOUT_LIMITS);
        balance.addPayment(date("2022-06-27"), amount("35.34"), WITHOUT_LIMITS);
        balance.addPayment(date("2022-09-14"), amount("35.34"), WITHOUT_LIMITS);
        balance.addPayment(date("2022-12-14"), amount("35.34"), WITHOUT_LIMITS);
        balance.addPayment(date("2023-05-29"), amount("35.34"), WITHOUT_LIMITS);
        balance.addPayment(date("2023-06-15"), amount("35.34"), WITHOUT_LIMITS);
        balance.addPayment(date("2023-09-14"), amount("35.34"), WITHOUT_LIMITS);

        System.out.println("koniec");
    }

    private ContractData contractDate(LocalDate start) {
        return new ContractData(AggregateId.generate(), start, null, WITHOUT_LIMITS, YearMonth.from(start));
    }

    private LocalDate date(String val) {
        return LocalDate.parse(val);
    }

    private BigDecimal amount(String val) {
        return new BigDecimal(val);
    }

}