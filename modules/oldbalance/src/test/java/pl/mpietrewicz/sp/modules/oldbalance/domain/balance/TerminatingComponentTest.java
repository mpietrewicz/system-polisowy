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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TerminatingComponentTest {

    BalanceAssembler balanceAssembler = new BalanceAssembler();
    PeriodAssembler periodAssembler = new PeriodAssembler();
    DueAssembler dueAssembler = new DueAssembler();
    ComponentAssembler componentAssembler = new ComponentAssembler();
    ComponentDueAssembler componentDueAssembler = new ComponentDueAssembler();
    FundAssembler fundAssembler;
    PeriodSnapshotAssembler periodSnapshotAssembler;
    Balance balance;

    @Test
    public void shouldIncreaseBalance() {
        ComponentData componentData = givenComponent().build().generateSnapshot();
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-01")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.OVERPAID)
                                .withOverpayment(10)
                                .build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-01-15")
                                .withAmount(20)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.OVERPAID)
                                                .withOverpayment(10)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().terminateComponent(componentData,
                LocalDate.parse("2022-12-31"));

        thenBalance().paidToEquals("2023-01"); // todo: raczej nie powinno już być okresów
        thenBalance().overpaymentEquals(20);
    }

    @Test
    public void shouldNotPaidPeriods() {
        ComponentData componentData = givenComponent().build().generateSnapshot();
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-01")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.PAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-02")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNPAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-03")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNPAID)
                                .build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-02-01")
                                .withAmount(10)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.PAID)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().terminateComponent(componentData,
                LocalDate.parse("2023-01-31"));

        thenBalance().paidToEquals("2023-01");
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldNotPaidPeriodsWithTwoPayments() {
        ComponentData componentData = givenComponent().build().generateSnapshot();
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-01")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.PAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-02")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNPAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-03")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNPAID)
                                .build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-02-01")
                                .withAmount(6)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.UNDERPAID)
                                                .withUnderpayment(4)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build(),
                        givenFund()
                                .withDate("2023-03-13")
                                .withAmount(4)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.PAID)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().terminateComponent(componentData,
                LocalDate.parse("2023-01-31"));

        thenBalance().paidToEquals("2023-01");
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldNotPaidPeriodsWithCoveredPeriod() {
        ComponentData componentData = givenComponent().build().generateSnapshot();
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-01")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.PAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-02")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.PAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-03")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNDERPAID)
                                .withUnderpayment(5)
                                .build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-02-01")
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
                                .withDate("2023-03-13")
                                .withAmount(10)
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
                ).collect(Collectors.toList()));

        whenBalance().terminateComponent(componentData,
                LocalDate.parse("2023-01-31"));

        thenBalance().paidToEquals("2023-03");
        thenBalance().overpaymentEquals(15);
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

    private BalanceAssembler givenBalance() {
        return balanceAssembler;
    }

    private ComponentAssembler givenComponent() {
        return componentAssembler;
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