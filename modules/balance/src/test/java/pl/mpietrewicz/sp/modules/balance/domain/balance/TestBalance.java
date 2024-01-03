package pl.mpietrewicz.sp.modules.balance.domain.balance;

import org.junit.Test;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.time.LocalDate;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.CONTINUATION;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.NO_RENEWAL;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.RENEWAL;

public class TestBalance {

    @Test
    public void newTest() {
        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, Amount.TEN, contractData.getAggregateId());
        balance.addPayment(date("2023-02-10"), amount("30"), CONTINUATION);
        balance.addRefund(date("2023-03-10"), amount("20"));
        balance.addPayment(date("2023-05-20"), amount("60"), CONTINUATION);
        balance.changePremium(LocalDate.parse("2023-02-01"), new Amount("100"), contractData.getAggregateId());

        System.out.println("koniec");
    }

    @Test
    public void testProductionExample() {
        LocalDate contractStart = LocalDate.parse("2022-03-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, new Amount("11.78"), contractData.getAggregateId());
        balance.addPayment(date("2022-02-25"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2022-06-27"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2022-09-14"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2022-12-14"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2023-05-29"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2023-06-15"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2023-09-14"), amount("35.34"), CONTINUATION);

        System.out.println("koniec");
    }

    @Test
    public void testNonRenewalPolicy() {
        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, Amount.TEN, contractData.getAggregateId());
        balance.addPayment(date("2023-02-10"), amount("30"), NO_RENEWAL);
        balance.addRefund(date("2023-03-10"), amount("20"));
        balance.addPayment(date("2023-05-20"), amount("60"), NO_RENEWAL);

        System.out.println("koniec");
    }

    @Test
    public void testRenewalPolicy() { // todo: jest błąd, bo ostani okres jest UNDERPAID, a powinien być UNPAID
        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, Amount.TEN, contractData.getAggregateId());
        balance.addPayment(date("2023-02-10"), amount("30"), NO_RENEWAL);
        balance.addRefund(date("2023-03-10"), amount("20"));
        balance.addPayment(date("2023-10-20"), amount("60"), RENEWAL);

        System.out.println("koniec");
    }

    private ContractData contractDate(LocalDate start) {
        return new ContractData(AggregateId.generate(), start, null, CONTINUATION, YearMonth.from(start));
    }

    private LocalDate date(String val) {
        return LocalDate.parse(val);
    }

    private Amount amount(String val) {
        return new Amount(val);
    }

}