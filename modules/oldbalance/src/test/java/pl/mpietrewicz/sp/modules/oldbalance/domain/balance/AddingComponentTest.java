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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddingComponentTest {

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
    public void shouldDecreaseExcess() {
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
                                .withStatus(PeriodStatus.OVERPAID)
                                .withOverpayment(10)
                                .build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-01-15")
                                .withAmount(30)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-01")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-02")
                                                .withStatus(PeriodStatus.OVERPAID)
                                                .withOverpayment(10)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().addComponent(givenComponent()
                        .withStartDate(LocalDate.parse("2023-02-01"))
                        .build()
                        .generateSnapshot(),
                new BigDecimal(15));

        thenBalance().paidToEquals("2023-01");
        thenBalance().underpaymentEquals(5);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldBackAllPeriods() {
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-03")
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
                                .withMonth("2023-04")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.PAID)
                                .build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-04-14")
                                .withAmount(20)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-03")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-04")
                                                .withStatus(PeriodStatus.PAID)
                                                .withOverpayment(10)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().addComponent(givenComponent()
                        .withStartDate(LocalDate.parse("2023-03-01"))
                        .build()
                        .generateSnapshot(),
                new BigDecimal(15));

        thenBalance().paidToEquals("2023-02");
        thenBalance().underpaymentEquals(30);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldBackOnePeriod() {
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-03")
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
                                .withMonth("2023-04")
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
                                .withDate("2023-04-14")
                                .withAmount(30)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-03")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-04")
                                                .withStatus(PeriodStatus.OVERPAID)
                                                .withOverpayment(10)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().addComponent(givenComponent()
                        .withStartDate(LocalDate.parse("2023-04-01"))
                        .build()
                        .generateSnapshot(),
                new BigDecimal(25));

        thenBalance().paidToEquals("2023-03");
        thenBalance().underpaymentEquals(15);
        thenBalance().overpaymentEquals(0);
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