package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import org.junit.Ignore;
import org.junit.Test;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.BalanceAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.BalanceAssert;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.ComponentAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.ComponentDueAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.DueAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.FundAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.PeriodAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.PeriodSnapshotAssembler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PayingWithRenewalTest {

    BalanceAssembler balanceAssembler = new BalanceAssembler();
    PeriodAssembler periodAssembler = new PeriodAssembler();
    DueAssembler dueAssembler = new DueAssembler();
    ComponentAssembler componentAssembler = new ComponentAssembler();
    ComponentDueAssembler componentDueAssembler = new ComponentDueAssembler();
    FundAssembler fundAssembler;
    PeriodSnapshotAssembler periodSnapshotAssembler;
    ComponentData componentData = givenComponent().build().generateSnapshot();
    Balance balance;

    @Test
    public void shouldPaidFirstTrhreMonths() {
        givenBalance()
                .withPeriods(List.of(
                        preparePeriod("2023-01", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-02", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-03", 10, PeriodStatus.UNPAID).build()
                ));

        whenBalance().addPayment(preparePayment("2023-01-12", 30));

        thenBalance().paidToEquals("2023-03");
        thenBalance().underpaymentEquals(0);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldPaidWithExcess() {
        givenBalance()
                .withPeriods(List.of(
                        preparePeriod("2023-01", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-02", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-03", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-04", 10, PeriodStatus.UNPAID).build()
                ));

        whenBalance().addPayment(preparePayment("2023-02-25", 40));

        thenBalance().paidToEquals("2023-04");
        thenBalance().underpaymentEquals(0);
        thenBalance().overpaymentEquals(30);
    }

    @Test
    public void shouldPaidExtendVeriosn() {
        givenBalance()
                .withPeriods(List.of(
                        preparePeriod("2023-01", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-02", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-03", 10, PeriodStatus.UNDERPAID).withUnderpayment(5).build(),
                        preparePeriod("2023-04", 10, PeriodStatus.UNPAID).build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-01-15")
                                .withAmount(12)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-02")
                                                .withStatus(PeriodStatus.UNDERPAID)
                                                .withUnderpayment(8)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build(),
                        givenFund()
                                .withDate("2023-01-29")
                                .withAmount(13)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-02")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-03")
                                                .withStatus(PeriodStatus.UNDERPAID)
                                                .withUnderpayment(5)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()))
        ;

        whenBalance().addPayment(preparePayment("2023-01-19", 30));

        thenBalance().paidToEquals("2023-04");
        thenBalance().underpaymentEquals(0);
        thenBalance().overpaymentEquals(15);
    }

    @Test
    public void shouldPaidAfterBreak() {
        givenBalance()
                .withPeriods(List.of(
                        preparePeriod("2023-01", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-02", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-03", 10, PeriodStatus.UNDERPAID).withUnderpayment(5).build(),
                        preparePeriod("2023-04", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-05", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-06", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-07", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-08", 10, PeriodStatus.UNPAID).build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-01-15")
                                .withAmount(12)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-02")
                                                .withStatus(PeriodStatus.UNDERPAID)
                                                .withUnderpayment(8)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build(),
                        givenFund()
                                .withDate("2023-01-29")
                                .withAmount(13)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-02")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-03")
                                                .withStatus(PeriodStatus.UNDERPAID)
                                                .withUnderpayment(5)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()))
        ;

        whenBalance().addPayment(preparePayment("2023-07-20", 30));

        thenBalance().paidToEquals("2023-08");
        thenBalance().underpaymentEquals(0);
        thenBalance().overpaymentEquals(10);
    }

    @Ignore
    @Test
    public void shouldPaidBewteen() {
        givenBalance()
                .withPeriods(List.of(
                        preparePeriod("2023-01", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-02", 10, PeriodStatus.UNDERPAID).withUnderpayment(5).build(),
                        preparePeriod("2023-03", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-04", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-05", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-06", 10, PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-07", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-08", 10, PeriodStatus.PAID).build(),
                        preparePeriod("2023-09", 10, PeriodStatus.OVERPAID).withOverpayment(15).build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-01-15")
                                .withAmount(15)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-02")
                                                .withStatus(PeriodStatus.UNDERPAID)
                                                .withUnderpayment(5)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build(),
                        givenFund()
                                .withDate("2023-07-07")
                                .withAmount(45)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-07")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-08")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-09")
                                                .withStatus(PeriodStatus.OVERPAID)
                                                .withOverpayment(15)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()))
        ;

        whenBalance().addPayment(preparePayment("2023-06-06", 5));

        thenBalance().paidToEquals("2023-09");
        thenBalance().overpaymentEquals(10);
    }

    private PeriodAssembler preparePeriod(String month, int premiumDue, PeriodStatus status) {
        return givenPeriod()
                .withMonth(month)
                .withDue(givenDue()
                        .withComponentDues(Arrays.asList(givenComponentDue()
                                .withComponentData(componentData)
                                .withPremiumDue(premiumDue)
                                .build()
                        )).build()
                )
                .withStatus(status);
    }

    private DueAssembler givenDue() {
        return dueAssembler;
    }

    private ComponentDueAssembler givenComponentDue() {
        return componentDueAssembler;
    }

    private ComponentAssembler givenComponent() {
        return componentAssembler;
    }

    private PeriodAssembler givenPeriod() {
        return periodAssembler;
    }

    private FundAssembler givenFund() {
        return fundAssembler = new FundAssembler();
    }

    private PeriodSnapshotAssembler givenPeriodSnapshot() {
        return periodSnapshotAssembler = new PeriodSnapshotAssembler();
    }

    private BalanceAssembler givenBalance() {
        return balanceAssembler;
    }

    private Balance whenBalance() {
        balance = balanceAssembler.build();
        return balance;
    }

    private PaymentData preparePayment(String date, int amount) { // todo: do zmiany w przyszłości
        return new PaymentData(AggregateId.generate(), null, LocalDate.parse(date), new BigDecimal(amount));
    }

    private BalanceAssert thenBalance() {
        return new BalanceAssert(balance);
    }
}