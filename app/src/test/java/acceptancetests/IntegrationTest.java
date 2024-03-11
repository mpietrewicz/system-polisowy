package acceptancetests;

import acceptancetests.dataread.JsonReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.mpietrewicz.sp.app.SpringbootApplication;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.PaymentData;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.CONTINUATION;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.RENEWAL_WITH_UNDERPAYMENT;

@SpringBootTest
@ContextConfiguration(classes = {SpringbootApplication.class} )
public class IntegrationTest {

    private static ContractData contractData;

    @Inject
    ContractService contractService;

    @Inject
    ComponentService componentService;

    @Inject
    BalanceService balanceService;

    @Inject
    PremiumService premiumService;

    @Inject
    ComponentRepository componentRepository;

    @Inject
    BalanceRepository balanceRepository;

    @Test
    public void productionTestNew() throws IOException {
        JsonReader jsonReader = new JsonReader();
        List<NowyPakiet> daneDoTestow = jsonReader.read();

        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);
        Balance balance = new Balance(AggregateId.generate(), 0L, contractData.getAggregateId(), new ArrayList<>());

        List<ContractOperation> sortedOperations = daneDoTestow.stream()
                .filter(nowyPakiet -> nowyPakiet.getIdUmowy().equals("0350/D/204872"))
                .findAny()
                .get()
                .getNoweSkladniki().stream()
                .map(NowySkladnik::getContractOperations)
                .flatMap(Collection::stream)
                .filter(c -> c.getDATA_ZMIANY() != null)
                .sorted(Comparator.comparing(ContractOperation::getDATA_REJESTRACJI)
                        .thenComparing(ContractOperation::getDATA_ZMIANY))
                .collect(Collectors.toList());

        for (ContractOperation operation : sortedOperations) {
            newRunBalanceMethod(balance, operation);
        }

        System.out.println("koniec");
    }

    private ContractData contractDate(LocalDate start) {
        return new ContractData(AggregateId.generate(), start, null, CONTINUATION, YearMonth.from(start));
    }

    private void newRunBalanceMethod(Balance balance, ContractOperation contractOperation) {
        LocalDate dataZmiany = convertToLocalDate(contractOperation.getDATA_ZMIANY());
        PositiveAmount kwota = contractOperation.getKTOWA() == null
                ? PositiveAmount.ZERO
                : new PositiveAmount(contractOperation.getKTOWA().replace(",", "."));
        if (contractOperation.getOPERACJA().equals("ZUM")
                || (contractOperation.getOPERACJA().equals("PUM") && contractOperation.getRODZAJ_SKL().equals("PODST"))) {
            String number = contractOperation.getNR_SKLADNIKA();
            Contract contract = contractService.createContract(number, dataZmiany, kwota, Frequency.QUARTERLY, RENEWAL_WITH_UNDERPAYMENT);
            contractData = contract.generateSnapshot();
        } else if (List.of("Wplata").contains(contractOperation.getOPERACJA())) {
            balanceService.addPayment(
                    new PaymentData(AggregateId.generate(), contractData.getAggregateId(), dataZmiany, kwota.getAmount()),
                    RENEWAL_WITH_UNDERPAYMENT
            );
        } else if (List.of("Dofinansowanie").contains(contractOperation.getOPERACJA())) {
            balanceService.addPayment(
                    new PaymentData(AggregateId.generate(), contractData.getAggregateId(), dataZmiany, kwota.getAmount()),
                    CONTINUATION
            );
        } else if (List.of("PSU").contains(contractOperation.getOPERACJA())) {
            List<Component> components = componentRepository.findByContractId(contractData.getAggregateId());
            Component basicComponent = components.stream().filter(not(Component::isAdditional)).findAny().orElseThrow();
            premiumService.change(basicComponent.getAggregateId(), dataZmiany, kwota);
        } else if (List.of("DSK").contains(contractOperation.getOPERACJA())
                || (contractOperation.getOPERACJA().equals("PUM") && contractOperation.getRODZAJ_SKL().equals("DOD"))) {
            String number = contractOperation.getNR_SKLADNIKA();
            componentService.addComponent(contractData.getAggregateId(), number, dataZmiany, kwota);
        } else if (List.of("ZOU").contains(contractOperation.getOPERACJA())) {
            String number = contractOperation.getNR_SKLADNIKA();
            componentService.terminate(number, dataZmiany);
        } else if (List.of("ZOU_P").contains(contractOperation.getOPERACJA())) {
            balanceService.stopCalculating(dataZmiany, contractData);
        } else if (List.of("WZOU_P").contains(contractOperation.getOPERACJA())) {
            balanceService.cancelStopCalculating(contractData);
        } else if (List.of("USK").contains(contractOperation.getOPERACJA())) {
            String number = contractOperation.getNR_SKLADNIKA();
            premiumService.cancel(number);
        }
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}