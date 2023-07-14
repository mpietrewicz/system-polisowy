package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

import org.junit.Test;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RefundingAmountTest {

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
    public void shouldRefundAll() {
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
                                .withStatus(PeriodStatus.UNDERPAID)
                                .withUnderpayment(5)
                                .build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-03-18")
                                .withAmount(15)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-03")
                                                .withStatus(PeriodStatus.PAID)
                                                .build(),
                                        givenPeriodSnapshot()
                                                .withMonth("2023-04")
                                                .withStatus(PeriodStatus.UNDERPAID)
                                                .withUnderpayment(5)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().addRefund(new RefundData(null, null, null, new BigDecimal(15)));

        thenBalance().paidToEquals("2023-02");
        thenBalance().underpaymentEquals(20);
    }

    @Test
    public void shouldRefundNotAll() {
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-01")
                                .withDue(givenDue()
                                        .withComponentDues(Arrays.asList(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(20)
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
                                .withStatus(PeriodStatus.UNDERPAID)
                                .withUnderpayment(5)
                                .build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-02-12")
                                .withAmount(25)
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
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().addRefund(new RefundData(null, null, null, new BigDecimal(15)));

        thenBalance().isNotNull();
        thenBalance().paidToEquals("2022-12");
        thenBalance().underpaymentEquals(20);
    }

    @Test
    public void shouldRefundPartOfOverpayment() {
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
                                .withOverpayment(20)
                                .build()
                ))
                .withFunds(Stream.of(
                        givenFund()
                                .withDate("2023-01-01")
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
                                .withDate("2023-02-12")
                                .withAmount(25)
                                .withPaidPeriods(Stream.of(
                                        givenPeriodSnapshot()
                                                .withMonth("2023-02")
                                                .withStatus(PeriodStatus.OVERPAID)
                                                .withUnderpayment(20)
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()));

        whenBalance().addRefund(new RefundData(null, null, null, new BigDecimal(15)));

        thenBalance().isNotNull();
        thenBalance().paidToEquals("2023-02");
        thenBalance().overpaymentEquals(5);
    }


    private ComponentAssembler givenComponent() {
        return componentAssembler;
    }

    private ComponentDueAssembler givenComponentDue() {
        return componentDueAssembler;
    }

    private DueAssembler givenDue() {
        return dueAssembler;
    }

    private PeriodAssembler givenPeriod() {
        return periodAssembler;
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