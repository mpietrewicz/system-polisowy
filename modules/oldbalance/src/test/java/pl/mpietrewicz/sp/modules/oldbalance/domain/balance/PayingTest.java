package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

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
import pl.mpietrewicz.sp.modules.oldbalance.domain.utils.PeriodAssembler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PayingTest {

    BalanceAssembler balanceAssembler;
    DueAssembler dueAssembler;
    ComponentAssembler componentAssembler;
    ComponentDueAssembler componentDueAssembler;
    PeriodAssembler periodAssembler;
    ComponentData componentData = givenComponent().build().generateSnapshot();
    Balance balance;

    @Test
    public void shouldPaidExact() {
        givenBalance()
                .withPeriods(List.of(givenPeriod()
                        .withMonth("2023-01")
                        .withDue(givenDue()
                                .withComponentDues(List.of(givenComponentDue()
                                        .withComponentData(componentData)
                                        .withPremiumDue(10)
                                        .build()
                                )).build()
                        )
                        .withStatus(PeriodStatus.UNPAID)
                        .build()
                ));

        whenBalance().addPayment(preparePayment("2023-01-01", 20));

        thenBalance().paidToEquals("2023-01");
        thenBalance().underpaymentEquals(0);
        thenBalance().overpaymentEquals(10);
    }

    @Test
    public void shouldPaidTooMuch() {
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-03")
                                .withDue(givenDue()
                                        .withComponentDues(List.of(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNPAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-04")
                                .withDue(givenDue()
                                        .withComponentDues(List.of(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNPAID)
                                .build()
                ));

        whenBalance().addPayment(preparePayment("2023-03-12", 25));

        thenBalance().isNotNull();
        thenBalance().paidToEquals("2023-04");
        thenBalance().overpaymentEquals(5);
    }

    @Test
    public void shouldPaidNotEnough() {
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-05")
                                .withDue(givenDue()
                                        .withComponentDues(List.of(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(10)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNPAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-06")
                                .withDue(givenDue()
                                        .withComponentDues(List.of(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(15)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNPAID)
                                .build()
                ));

        whenBalance().addPayment(preparePayment("2023-05-01", 15));

        thenBalance().isNotNull();
        thenBalance().paidToEquals("2023-05");
        thenBalance().underpaymentEquals(10);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldOverpay() {
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-01")
                                .withDue(givenDue()
                                        .withComponentDues(List.of(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(15)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.PAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-02")
                                .withDue(givenDue()
                                        .withComponentDues(List.of(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(15)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNDERPAID)
                                .withUnderpayment(5)
                                .build()
                ));

        whenBalance().addPayment(preparePayment("2023-02-11", 15));

        thenBalance().isNotNull();
        thenBalance().paidToEquals("2023-02");
        thenBalance().underpaymentEquals(0);
        thenBalance().overpaymentEquals(10);
    }

    @Test
    public void shouldNotPayThePeriod() {
        givenBalance()
                .withPeriods(List.of(
                        givenPeriod()
                                .withMonth("2023-01")
                                .withDue(givenDue()
                                        .withComponentDues(List.of(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(30)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.PAID)
                                .build(),
                        givenPeriod()
                                .withMonth("2023-02")
                                .withDue(givenDue()
                                        .withComponentDues(List.of(givenComponentDue()
                                                .withComponentData(componentData)
                                                .withPremiumDue(45)
                                                .build()
                                        )).build()
                                )
                                .withStatus(PeriodStatus.UNDERPAID)
                                .withUnderpayment(35)
                                .build()
                ));

        whenBalance().addPayment(preparePayment("2023-02-01", 30));

        thenBalance().isNotNull();
        thenBalance().paidToEquals("2023-01");
        thenBalance().underpaymentEquals(5);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldUnderpaid() {
        givenBalance()
                .withPeriods(List.of(givenPeriod()
                        .withMonth("2023-01")
                        .withDue(givenDue()
                                .withComponentDues(List.of(givenComponentDue()
                                        .withComponentData(componentData)
                                        .withPremiumDue(10)
                                        .build()
                                )).build()
                        )
                        .withStatus(PeriodStatus.UNPAID)
                        .build()
                ));

        whenBalance().addPayment(preparePayment("2023-01-31",8));

        thenBalance().paidToEquals("2022-12");
        thenBalance().underpaymentEquals(2);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldPaid() {
        givenBalance()
                .withPeriods(List.of(givenPeriod()
                        .withMonth("2023-01")
                        .withDue(givenDue()
                                .withComponentDues(List.of(givenComponentDue()
                                        .withComponentData(componentData)
                                        .withPremiumDue(10)
                                        .build()
                                )).build()
                        )
                        .withStatus(PeriodStatus.UNPAID)
                        .build()
                ));

        whenBalance().addPayment(preparePayment("2023-01-01",10));

        thenBalance().paidToEquals("2023-01");
        thenBalance().underpaymentEquals(0);
        thenBalance().overpaymentEquals(0);
    }

    @Test
    public void shouldOverpaid() {
        givenBalance()
                .withPeriods(List.of(givenPeriod()
                        .withMonth("2023-01")
                        .withDue(givenDue()
                                .withComponentDues(List.of(givenComponentDue()
                                        .withComponentData(componentData)
                                        .withPremiumDue(10)
                                        .build()
                                )).build()
                        )
                        .withStatus(PeriodStatus.UNPAID)
                        .build()
                ));

        whenBalance().addPayment(preparePayment("2023-01-22",14));

        thenBalance().paidToEquals("2023-01");
        thenBalance().underpaymentEquals(0);
        thenBalance().overpaymentEquals(4);
    }

    private DueAssembler givenDue() {
        return dueAssembler = new DueAssembler();
    }

    private ComponentDueAssembler givenComponentDue() {
        return componentDueAssembler = new ComponentDueAssembler();
    }

    private ComponentAssembler givenComponent() {
        return componentAssembler = new ComponentAssembler();
    }

    private PeriodAssembler givenPeriod() {
        return periodAssembler = new PeriodAssembler();
    }

    private BalanceAssembler givenBalance() {
        return balanceAssembler = new BalanceAssembler();
    }

    private Balance whenBalance() {
        balance = balanceAssembler.build();
        return balance;
    }

    private PaymentData preparePayment(String date, int amount) {
        return new PaymentData(AggregateId.generate(), null, LocalDate.parse(date), new BigDecimal(amount));
    }

    private BalanceAssert thenBalance() {
        return new BalanceAssert(balance);
    }
}