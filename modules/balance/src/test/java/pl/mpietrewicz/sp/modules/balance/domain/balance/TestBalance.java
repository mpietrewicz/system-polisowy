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
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy.WITHOUT_LIMITS;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy.WITHOUT_RENEWAL;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy.WITH_RENEWAL;

public class TestBalance {

    @Test
    public void testAllChanges() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = balanceFactory.create(
                contractDate(month("2023-10")),
                month("2023-10"),
                amount("5"), Frequency.QUARTERLY);

        balance.openMonth(YearMonth.parse("2024-01"));
        balance.addPremium(date("2024-01-01"), amount("10"));
        balance.openMonth(YearMonth.parse("2024-04"));
        balance.openMonth(YearMonth.parse("2024-07"));
        balance.addPremium(date("2024-06-01"), amount("20"));
        balance.deletePremium(date("2024-05-01"), amount("8"));
        balance.addPayment(date("2024-05-10"), amount("30"), WITHOUT_LIMITS);
        balance.addRefund(date("2024-05-15"), amount("29"));
        balance.addPayment(date("2024-05-20"), amount("60"), WITHOUT_LIMITS);
        balance.addPremium(date("2024-07-01"), amount("100"));
        balance.changeFrequency(date("2024-07-01"), Frequency.MONTHLY);
        balance.openMonth(YearMonth.parse("2024-08"));
        System.out.println("koniec");
    }

    private ContractData contractDate(YearMonth start) {
        return new ContractData(AggregateId.generate(), start.atDay(1), Frequency.QUARTERLY, WITHOUT_LIMITS);
    }


    @Test
    public void testUmowyZPrzeszlosci() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = balanceFactory.create(
                contractDate(month("2023-10")),
                month("2023-03"),
                amount("5"), Frequency.QUARTERLY);
        System.out.println("koniec");
    }


    @Test
    public void testUmowyZPszyszlosci() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = balanceFactory.create(
                contractDate(month("2023-10")),
                month("2023-12"),
                amount("5"), Frequency.QUARTERLY);
        balance.openMonth(month("2023-11"));
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
        Balance balance = balanceFactory.create(
                contractDate(month("2023-10")),
                month("2023-10"),
                amount("5"), Frequency.QUARTERLY);

        balance.addPremium(date("2023-11-01"), amount("10"));
        balance.addPremium(date("2023-12-01"), amount("20"));
        balance.openMonth(month("2023-11"));
        balance.openMonth(month("2023-12"));
        System.out.println("koniec");
    }

    @Test
    public void testWplatyZeWznowieniem() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = balanceFactory.create(
                contractDate(month("2023-10")),
                month("2023-10"),
                amount("10"), Frequency.QUARTERLY);

        balance.openMonth(month("2023-11"));
        balance.addPayment(date("2023-10-31"), amount("10"), WITHOUT_LIMITS);
        balance.openMonth(month("2023-12"));
        balance.openMonth(month("2024-01"));
        balance.openMonth(month("2024-02"));
        balance.openMonth(month("2024-03"));
        balance.addPayment(date("2024-03-01"), amount("20"), WITH_RENEWAL);
        balance.openMonth(month("2024-04"));
        System.out.println("koniec");
    }

//    @Test
    public void testWplatyZeBlokadaWznowienia() {
        BalanceFactory balanceFactory = new BalanceFactory();
        Balance balance = balanceFactory.create(
                contractDate(month("2023-10")),
                month("2023-10"),
                amount("10"), Frequency.QUARTERLY);

        balance.openMonth(month("2023-11"));
        balance.addPayment(date("2023-10-31"), amount("10"), WITHOUT_LIMITS);
        balance.openMonth(month("2023-12"));
        balance.openMonth(month("2024-01"));
        balance.openMonth(month("2024-02"));
        balance.openMonth(month("2024-03"));
        balance.addPayment(date("2024-03-01"), amount("20"), WITHOUT_RENEWAL);
        balance.openMonth(month("2024-04"));
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