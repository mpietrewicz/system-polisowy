package pl.mpietrewicz.sp.modules.contract.webUi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.mpietrewicz.sp.cqrs.command.Gate;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.contract.application.commands.AddComponentCommand;
import pl.mpietrewicz.sp.modules.contract.application.commands.ChangePremiumCommand;
import pl.mpietrewicz.sp.modules.contract.application.commands.RegisterContractCommand;
import pl.mpietrewicz.sp.modules.contract.application.commands.ShiftAccountingMonthCommand;
import pl.mpietrewicz.sp.modules.contract.application.commands.TerminateComponentCommand;
import pl.mpietrewicz.sp.modules.contract.readmodel.ContractFinder;
import pl.mpietrewicz.sp.modules.contract.readmodel.dto.PolisaDto;
import pl.mpietrewicz.sp.modules.contract.readmodel.dto.SkladnikDto;
import pl.mpietrewicz.sp.modules.contract.readmodel.dto.ZmianaSkladkiDto;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Getter
@RequiredArgsConstructor
@CrossOrigin(origins = {"${cors-origin}"})
@RestController
public class ContractController { // todo: poprawić nazwewnictwo, np. id (id czego?)

    @Inject
    private ContractFinder contractFinder;

    @Inject
    private Gate gate;

    @GetMapping("/lista-polis")
    public List<PolisaDto> pobierzWszystkiePolisy() {
        List<PolisaDto> polisaDtos = contractFinder.find();
//        polisaDtos.forEach(p -> p.setSkladka(getCurrentContractPremium(p.getId())));
        return polisaDtos;
    }

    @GetMapping("/dane-polisy/{id}")
    public PolisaDto pobierzDanePolisy(@PathVariable String id) {
        PolisaDto polisaDto = contractFinder.find(id);
        polisaDto.setSkladka(getCurrentContractPremium(id));
        return polisaDto;
    }

    @GetMapping("/lista-skladnikow/{id}")
    public List<SkladnikDto> pobierzListeSkladnikow(@PathVariable String id) {
        List<SkladnikDto> skladnikiDto = contractFinder.findComponents(id);
        for (SkladnikDto skladnik : skladnikiDto) {
            skladnik.setSkladka(getCurrentPremium(skladnik.getIdSkladnika()));
            skladnik.setOczekiwaneSkladki(getExceptedPremiumSum(skladnik.getIdSkladnika())); // todo: do zmiany!
        }
        return skladnikiDto;
    }

    @GetMapping("/dane-skladnika/{id}")
    public SkladnikDto pobierzDaneSkladnika(@PathVariable String id) {
        SkladnikDto skladnikDto = contractFinder.findComponent(id);
        skladnikDto.setSkladka(getCurrentPremium(skladnikDto.getIdSkladnika()));
        skladnikDto.setOczekiwaneSkladki(getExceptedPremiumSum(skladnikDto.getIdSkladnika())); // todo: do zmiany!
        return skladnikDto;
    }

    @PostMapping("/rejestracja-polisy")
    public String rejestrujPolise(@RequestBody PolisaDto polisaDto) {
        LocalDate registerDate = polisaDto.getDataRejestracji();
        Amount skladka = new Amount(polisaDto.getSkladka());
        Frequency czestotliwosc = polisaDto.getCzestotliwosc();
        PaymentPolicyEnum typ = polisaDto.getTyp(); // todo: czy to jest istotne?, czyba nie - to powinno dotyczyć wpłaty

        gate.dispatch(new RegisterContractCommand(registerDate, skladka, czestotliwosc, typ));
        return "redirect:/lista-polis";
    }

    @PostMapping("/zmiana-skladki")
    public void zmienSkladke(@RequestBody ZmianaSkladkiDto zmianaSkladkiDto) {
        String idSkladnika = zmianaSkladkiDto.getIdSkladnika();
        Amount skladka = new Amount(zmianaSkladkiDto.getSkladka());
        YearMonth dataZmiany = YearMonth.parse(zmianaSkladkiDto.getDataZmiany());

        gate.dispatch(new ChangePremiumCommand(idSkladnika, skladka, dataZmiany));
    }

    @PostMapping("/dokupienie-dodatku")
    public String dokupSkladnik(@RequestBody SkladnikDto skladnikDto) {
        String idUmowy = skladnikDto.getIdUmowy();
        LocalDate dataDokupienia = skladnikDto.getDataDokupienia();
        Amount skladka = new Amount(skladnikDto.getSkladka());

        gate.dispatch(new AddComponentCommand(idUmowy, dataDokupienia, skladka));
        return "redirect:/lista-polis";
    }

    @PostMapping("/zakonczenie-skladnika")
    public String zakonczenieSkladnika(@RequestBody SkladnikDto skladnikDto) {
        String idSkladnika = skladnikDto.getIdSkladnika();
        LocalDate dataZakonczenia = skladnikDto.getDataZakonczenia();

        gate.dispatch(new TerminateComponentCommand(idSkladnika, dataZakonczenia));
        return "redirect:/lista-polis";
    }

    @PostMapping("/zmiana-miesiaca-ksiegowego")
    public String zmianaMiesiacaKsiegowego(@RequestBody PolisaDto polisaDto) {
        String idPolisy = polisaDto.getId();

        gate.dispatch(new ShiftAccountingMonthCommand(idPolisy));
        return "redirect:/lista-polis";
    }

    private BigDecimal getCurrentContractPremium(String id) {
        return contractFinder.findContractPremium(id);
    }

    private BigDecimal getCurrentPremium(String componentId) {
        return contractFinder.findComponentPremium(componentId);
    }

    private BigDecimal getExceptedPremiumSum(String componentId) {
        return BigDecimal.ZERO;
    }

}