package pl.mpietrewicz.sp.modules.balance.domain.balance;

import org.junit.Test;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.migration.BalanceMigration;
import pl.mpietrewicz.sp.modules.balance.domain.balance.migration.BalanceOperation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.migration.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency.MONTHLY;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency.QUARTERLY;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy.WITHOUT_LIMITS;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy.WITHOUT_RENEWAL;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy.WITH_RENEWAL;

public class TestBalance {

    @Test
    public void newTest() {
        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, BigDecimal.TEN, QUARTERLY, contractData.getAggregateId());
        balance.addPayment(date("2023-02-10"), amount("30"), WITHOUT_LIMITS);
        balance.addRefund(date("2023-03-10"), amount("20"));
        balance.addPayment(date("2023-05-20"), amount("60"), WITHOUT_LIMITS);
        balance.stopCalculating(date("2023-05-31"), Frequency.MONTHLY);
//        balance.changePremium(LocalDate.parse("2023-02-01"), new BigDecimal(100), contractData.getAggregateId());

        System.out.println("koniec");
    }

    private ContractData contractDate(LocalDate start) {
        return new ContractData(AggregateId.generate(), start, Frequency.QUARTERLY, WITHOUT_LIMITS, YearMonth.from(start));
    }


    @Test
    public void testUmowyZPrzeszlosci() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = null;
//        Balance balance = balanceFactory.create(
//                contractDate(month("2023-10")),
//                amount("5"), Frequency.QUARTERLY, AggregateId.generate());
        System.out.println("koniec");
    }


    @Test
    public void testUmowyZPszyszlosci() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = null;
//        Balance balance = balanceFactory.create(
//                contractDate(month("2023-10")),
//                amount("5"), Frequency.QUARTERLY, AggregateId.generate());
        System.out.println("koniec");
    }

    @Test
    public void testProductionExample() {
        BalanceMigration balanceMigration = BalanceMigration.builder()
                .contractId("1")
                .balanceOperations(List.of(
                        new BalanceOperation(OperationType.START_CONTRACT, dateTime("2022-02-25"), date("2022-03-01"), amount("11.78"), MONTHLY),
                        new BalanceOperation(OperationType.PAYMENT, dateTime("2022-02-26"), date("2022-02-25"), amount("35.34"), null),
                        new BalanceOperation(OperationType.PAYMENT, dateTime("2022-06-28"), date("2022-06-27"), amount("35.34"), null),
                        new BalanceOperation(OperationType.PAYMENT, dateTime("2022-09-15"), date("2022-09-14"), amount("35.34"), null),
                        new BalanceOperation(OperationType.PAYMENT, dateTime("2022-12-15"), date("2022-12-14"), amount("35.34"), null),
                        new BalanceOperation(OperationType.PAYMENT, dateTime("2023-05-30"), date("2023-05-29"), amount("35.34"), null),
                        new BalanceOperation(OperationType.PAYMENT, dateTime("2023-06-16"), date("2023-06-15"), amount("35.34"), null),
                        new BalanceOperation(OperationType.PAYMENT, dateTime("2023-09-15"), date("2023-09-14"), amount("35.34"), null)
                ))
                .build();

        BalanceFactory balanceFactory = new BalanceFactory();
//        Balance balance = balanceFactory.create(month("2023-10"), balanceMigration);

        System.out.println("koniec");
    }

    @Test
    public void testChangesAfterAccountingMonth() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = null;
//        Balance balance = balanceFactory.create(
//                contractDate(month("2023-10")),
//                amount("5"), Frequency.QUARTERLY, AggregateId.generate());

        System.out.println("koniec");
    }

    @Test
    public void testWplatyZeWznowieniem() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = null;
//        Balance balance = balanceFactory.create(
//                contractDate(month("2023-10")),
//                amount("10"), Frequency.QUARTERLY, AggregateId.generate());

        balance.addPayment(date("2023-10-31"), amount("10"), WITHOUT_LIMITS);
        balance.addPayment(date("2024-03-01"), amount("20"), WITH_RENEWAL);
        System.out.println("koniec");
    }

//    @Test
    public void testWplatyZeBlokadaWznowienia() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = null;
//        Balance balance = balanceFactory.create(
//                contractDate(month("2023-10")),
//                amount("10"), Frequency.QUARTERLY, AggregateId.generate());

        balance.addPayment(date("2023-10-31"), amount("10"), WITHOUT_LIMITS);
        balance.addPayment(date("2024-03-01"), amount("20"), WITHOUT_RENEWAL);
        System.out.println("koniec");
    }

    private YearMonth month(String val) {
        return YearMonth.parse(val);
    }

    private LocalDate date(String val) {
        return LocalDate.parse(val);
    }

    private LocalDateTime dateTime(String val) {
        return LocalDateTime.parse(val + "T00:00:00");
    }

    private BigDecimal amount(String val) {
        return new BigDecimal(val);
    }

}