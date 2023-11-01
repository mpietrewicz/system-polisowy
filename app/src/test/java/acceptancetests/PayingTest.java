package acceptancetests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.mpietrewicz.sp.app.SpringbootApplication;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@SpringBootTest
@ContextConfiguration(classes = {SpringbootApplication.class} )
public class PayingTest {

    @Inject
    ContractService contractService;

    @Inject
    ComponentService componentService;

    @Inject
    BalanceService balanceService;

    @Inject
    PremiumService premiumService;

    @Test
    public void test() {
        Contract contract = contractService.createContract(
                LocalDate.parse("2023-01-01"),
                new BigDecimal("15"),
                Frequency.QUARTERLY,
                PaymentPolicy.WITH_RENEWAL
        );
        componentService.addComponent(
                contract.getAggregateId(),
                LocalDate.parse("2023-03-01"),
                new BigDecimal("12")
        );
        balanceService.addPayment(new PaymentData(
                AggregateId.generate(),
                contract.getAggregateId(),
                LocalDate.parse("2023-02-15"),
                new BigDecimal("65.8")), PaymentPolicy.WITH_RENEWAL
        );
        Component component = componentService.addComponent(
                contract.getAggregateId(),
                LocalDate.parse("2023-04-01"),
                new BigDecimal("10")
        );
        balanceService.addPayment(new PaymentData(
                AggregateId.generate(),
                contract.getAggregateId(),
                LocalDate.parse("2023-02-15"),
                new BigDecimal("20")), PaymentPolicy.WITH_RENEWAL
        );
        premiumService.change(
                component.getAggregateId(),
                LocalDate.parse("2023-06-01"),
                new BigDecimal("20")
        );
        balanceService.addRefund(new RefundData(
                AggregateId.generate(),
                contract.getAggregateId(),
                LocalDate.parse("2023-03-11"),
                new BigDecimal("11"))
        );
        balanceService.openNewMonth(
                contract.getAggregateId(),
                YearMonth.parse("2023-04")
        );
        balanceService.addPayment(new PaymentData(
                AggregateId.generate(),
                contract.getAggregateId(),
                LocalDate.parse("2023-05-05"),
                new BigDecimal("12.5")), PaymentPolicy.WITH_RENEWAL
        );
        balanceService.openNewMonth(
                contract.getAggregateId(),
                YearMonth.parse("2023-10")
        );
        balanceService.addPayment(new PaymentData(
                AggregateId.generate(),
                contract.getAggregateId(),
                LocalDate.parse("2023-09-05"),
                new BigDecimal("50")), PaymentPolicy.WITH_RENEWAL
        );
    }


}