package acceptancetests;

import acceptancetests.dataread.JsonReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.mpietrewicz.sp.app.SpringbootApplication;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.application.api.BalanceService;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.BalanceRepository;
import pl.mpietrewicz.sp.modules.contract.application.api.ComponentService;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;
import pl.mpietrewicz.sp.modules.contract.domain.component.Component;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;
import pl.mpietrewicz.sp.modules.finance.application.api.FinanceService;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

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

    @Inject
    FinanceService financeService;

    @Test
    public void productionTestNew() throws IOException {
        JsonReader jsonReader = new JsonReader();
        List<NowyPakiet> daneDoTestow = jsonReader.read();

        List<ContractOperation> sortedOperations = daneDoTestow.stream()
                .filter(nowyPakiet -> nowyPakiet.getIdUmowy().equals("0353/P/1871"))
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
            newRunBalanceMethod(operation);
        }

        System.out.println("koniec");
    }

    private void newRunBalanceMethod(ContractOperation contractOperation) {
        LocalDate dataZmiany = convertToLocalDate(contractOperation.getDATA_ZMIANY());
        PositiveAmount kwota = pobierzDodatniaKwote(contractOperation);
        if (contractOperation.getOPERACJA().equals("ZUM")
                || (contractOperation.getOPERACJA().equals("PUM") && contractOperation.getRODZAJ_SKL().equals("PODST"))) {
            String name = wyznaczNazweSkladnika(contractOperation);
            Contract contract = contractService.createContract("2250", name, dataZmiany, kwota, Frequency.QUARTERLY);
            contractData = contract.generateSnapshot();
        } else if (List.of("Wplata").contains(contractOperation.getOPERACJA())) {
            financeService.addPayment(contractData.getAggregateId(), kwota, dataZmiany);
        } else if (List.of("Dofinansowanie").contains(contractOperation.getOPERACJA())) {
            financeService.addSubsidy(contractData.getAggregateId(), kwota, dataZmiany);
        } else if (List.of("Zwrot").contains(contractOperation.getOPERACJA())) {
            balanceService.addRefund(contractData.getAggregateId(), kwota);
        } else if (List.of("PSU").contains(contractOperation.getOPERACJA())) {
            List<Component> components = componentRepository.findBy(contractData.getAggregateId());
            Component basicComponent = components.stream().filter(not(Component::isAdditional)).findAny().orElseThrow();
            premiumService.change(basicComponent.getAggregateId(), dataZmiany, kwota);
        } else if (List.of("DSK").contains(contractOperation.getOPERACJA())
                || (contractOperation.getOPERACJA().equals("PUM") && contractOperation.getRODZAJ_SKL().equals("DOD"))) {
            String name = wyznaczNazweSkladnika(contractOperation);
            componentService.addComponent(contractData.getAggregateId(), name, dataZmiany, kwota);
        } else if (List.of("ZOU").contains(contractOperation.getOPERACJA())) {
            Component component = componentRepository.findBy(contractData.getAggregateId(), wyznaczNazweSkladnika(contractOperation)).get();
            componentService.terminate(component.getAggregateId(), dataZmiany);
        } else if (List.of("ZOU_P").contains(contractOperation.getOPERACJA())) {
            contractService.endContract(contractData.getAggregateId(), dataZmiany);
        } else if (List.of("WZOU_P").contains(contractOperation.getOPERACJA())) {
            contractService.cancelEndContract(contractData.getAggregateId());
        } else if (List.of("USK").contains(contractOperation.getOPERACJA())) {
            Component component = componentRepository.findBy(contractData.getAggregateId(), wyznaczNazweSkladnika(contractOperation)).get();
            premiumService.cancel(component.getAggregateId());
        }
    }

    private String wyznaczNazweSkladnika(ContractOperation contractOperation) {
        return contractOperation.getNR_SKLADNIKA().replace("/", "-");
    }

    private PositiveAmount pobierzDodatniaKwote(ContractOperation contractOperation) {
        try {
            return contractOperation.getKTOWA() == null
                    ? null
                    : PositiveAmount.withValue(contractOperation.getKTOWA().replace(",", "."));
        } catch (NotPositiveAmountException exception) {
            throw new IllegalStateException();
        }
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}