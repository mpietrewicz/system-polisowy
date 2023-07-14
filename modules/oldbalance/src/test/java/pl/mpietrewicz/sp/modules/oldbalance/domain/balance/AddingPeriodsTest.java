package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import org.junit.Test;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.PeriodStatus;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.BalanceAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.BalanceAssert;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.ComponentAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.ComponentDueAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.DueAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.FundAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.PeriodAssembler;
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.PeriodSnapshotAssembler;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddingPeriodsTest {

    BalanceAssembler balanceAssembler = new BalanceAssembler();
    ComponentAssembler componentAssembler = new ComponentAssembler();
    PeriodAssembler periodAssembler = new PeriodAssembler();
    DueAssembler dueAssembler = new DueAssembler();
    ComponentDueAssembler componentDueAssembler = new ComponentDueAssembler();
    FundAssembler fundAssembler;
    PeriodSnapshotAssembler periodSnapshotAssembler;
    ComponentData componentData = givenComponent().build().generateSnapshot();
    Balance balance;

    @Test
    public void shouldNotPaidAnything() {
        givenBalance()
                .withPeriods(Stream.of(
                        preparePeriod("2023-01", PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-02", PeriodStatus.UNPAID).build(),
                        preparePeriod("2023-03", PeriodStatus.UNPAID).build()
                ).collect(Collectors.toList()));

        whenBalance().addPeriods(3);

        thenBalance().paidToEquals("2022-12");
        thenBalance().underpaymentEquals(60);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldShiftIncreaseUnderpayment() {
        givenBalance()
                .withPeriods(Stream.of(
                        preparePeriod("2023-01", PeriodStatus.PAID).build(),
                        preparePeriod("2023-02", PeriodStatus.PAID).build(),
                        preparePeriod("2023-03", PeriodStatus.UNDERPAID).withUnderpayment(5).build()
                ).collect(Collectors.toList()))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-02-10")
                                .withAmount(25)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
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
                ).collect(Collectors.toList()));

        whenBalance().addPeriods(3);

        thenBalance().paidToEquals("2023-02");
        thenBalance().underpaymentEquals(35);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldPaidNewPeriods() {
        givenBalance()
                .withPeriods(Stream.of(
                        preparePeriod("2023-01", PeriodStatus.PAID).build(),
                        preparePeriod("2023-02", PeriodStatus.PAID).build(),
                        preparePeriod("2023-03", PeriodStatus.OVERPAID).withOverpayment(23).build()
                ).collect(Collectors.toList()))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-02-10")
                                .withAmount(25)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
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
                                .build(),
                        givenFund()
                                .withDate("2023-03-31")
                                .withAmount(28)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-03")
                                                .withStatus(PeriodStatus.OVERPAID)
                                                .withOverpayment(23)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().addPeriods(3);

        thenBalance().paidToEquals("2023-05");
        thenBalance().underpaymentEquals(7);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldPaidAllPeriods() {
        givenBalance()
                .withPeriods(Stream.of(
                        preparePeriod("2023-01", PeriodStatus.PAID).build(),
                        preparePeriod("2023-02", PeriodStatus.PAID).build(),
                        preparePeriod("2023-03", PeriodStatus.OVERPAID).withOverpayment(40).build()
                ).collect(Collectors.toList()))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-02-10")
                                .withAmount(70)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-02")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-03")
                                                .withStatus(PeriodStatus.OVERPAID)
                                                .withOverpayment(40)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().addPeriods(3);

        thenBalance().paidToEquals("2023-06");
        thenBalance().underpaymentEquals(0);
        thenBalance().overpaymentEquals(10);
    }

    private PeriodAssembler preparePeriod(String month, PeriodStatus status) {
        return givenPeriod()
                .withMonth(month)
                .withDue(givenDue()
                        .withComponentDues(Arrays.asList(givenComponentDue()
                                .withComponentData(componentData)
                                .withPremiumDue(10)
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

    private PeriodAssembler givenPeriod() {
        return periodAssembler;
    }

    private ComponentAssembler givenComponent() {
        return componentAssembler;
    }

    private BalanceAssembler givenBalance() {
        return balanceAssembler;
    }

    private FundAssembler givenFund() {
        return fundAssembler = new FundAssembler();
    }

    private PeriodSnapshotAssembler givenPeriodSnapshot() {
        return periodSnapshotAssembler = new PeriodSnapshotAssembler();
    }

    private Balance whenBalance() {
        balance = balanceAssembler.build();
        return balance;
    }

    private BalanceAssert thenBalance() {
        return new BalanceAssert(balance);
    }

}