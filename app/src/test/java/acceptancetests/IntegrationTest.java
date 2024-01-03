package acceptancetests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.mpietrewicz.sp.app.SpringbootApplication;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.readmodel.dto.PolisaDto;
import pl.mpietrewicz.sp.modules.contract.readmodel.dto.SkladnikDto;
import pl.mpietrewicz.sp.modules.contract.webUi.ContractController;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = {SpringbootApplication.class} )
public class IntegrationTest {

    @Inject
    ContractController contractController;

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
        PolisaDto polisaDto = PolisaDto.builder()
                .id("P/1")
                .dataRejestracji(LocalDate.parse("2023-01-01"))
                .skladka(new BigDecimal("20"))
                .czestotliwosc(Frequency.QUARTERLY)
                .typ(PaymentPolicyEnum.CONTINUATION)
                .build();
        contractController.rejestrujPolise(polisaDto);

        List<PolisaDto> polisaDtos = contractController.pobierzWszystkiePolisy();
        String idAgregatuUmowy = polisaDtos.stream()
                .findAny()
                .map(PolisaDto::getId)
                .orElseThrow();

        SkladnikDto skladnikDto = SkladnikDto.builder()
                .idUmowy(idAgregatuUmowy)
                .dataDokupienia(LocalDate.parse("2023-03-01"))
                .skladka(new BigDecimal("5.40"))
                .build();
        contractController.dokupSkladnik(skladnikDto);


//        Contract contract = contractService.createContract(
//                LocalDate.parse("2023-01-01"),
//                new Amount("15"),
//                Frequency.QUARTERLY,
//                PaymentPolicyEnum.NO_LIMITS
//        );
//        componentService.addComponent(
//                contract.getAggregateId(),
//                LocalDate.parse("2023-03-01"),
//                new Amount("12")
//        );
//        balanceService.addPayment(new PaymentData(
//                AggregateId.generate(),
//                contract.getAggregateId(),
//                LocalDate.parse("2023-02-15"),
//                new Amount("65.8")), PaymentPolicyEnum.NO_LIMITS
//        );
//        Component component = componentService.addComponent(
//                contract.getAggregateId(),
//                LocalDate.parse("2023-04-01"),
//                new Amount("10")
//        );
//        balanceService.addPayment(new PaymentData(
//                AggregateId.generate(),
//                contract.getAggregateId(),
//                LocalDate.parse("2023-02-15"),
//                new Amount("20")), PaymentPolicyEnum.NO_LIMITS
//        );
//        premiumService.change(
//                component.getAggregateId(),
//                LocalDate.parse("2023-06-01"),
//                new Amount("20")
//        );
//        balanceService.addRefund(new RefundData(
//                AggregateId.generate(),
//                contract.getAggregateId(),
//                LocalDate.parse("2023-03-11"),
//                new Amount("11"))
//        );
//        balanceService.addPayment(new PaymentData(
//                AggregateId.generate(),
//                contract.getAggregateId(),
//                LocalDate.parse("2023-05-05"),
//                new Amount("12.5")), PaymentPolicyEnum.NO_LIMITS
//        );
//        balanceService.addPayment(new PaymentData(
//                AggregateId.generate(),
//                contract.getAggregateId(),
//                LocalDate.parse("2023-09-05"),
//                new Amount("50")), PaymentPolicyEnum.NO_LIMITS
//        );
    }


}