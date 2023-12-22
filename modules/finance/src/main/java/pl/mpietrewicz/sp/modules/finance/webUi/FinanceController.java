package pl.mpietrewicz.sp.modules.finance.webUi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.mpietrewicz.sp.cqrs.command.Gate;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.finance.application.commands.RegisterPaymentCommand;
import pl.mpietrewicz.sp.modules.finance.readmodel.FinanceFinder;
import pl.mpietrewicz.sp.modules.finance.readmodel.dto.PrzypisDto;
import pl.mpietrewicz.sp.modules.finance.readmodel.dto.SaldoDto;
import pl.mpietrewicz.sp.modules.finance.readmodel.dto.WplataDto;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
@CrossOrigin(origins = {"${cors-origin}"})
@RestController
public class FinanceController {

    private FinanceFinder financeFinder = null;

    @Inject
    private Gate gate;

    @GetMapping("/lista-wplat/{id}")
    public List<WplataDto> pobierzWplaty(@PathVariable String id) {
        return financeFinder.findPayments(id);
    }

    @GetMapping("/przypis/{componentId}")
    public List<PrzypisDto> pobierzPrzypis(@PathVariable String componentId) {
//        return financeFinder.findPremiumDue(componentId);
        return null;
    }

    @GetMapping("/saldo/{id}")
    public SaldoDto pobierzSaldo(@PathVariable String id) {
        return new SaldoDto();
    }

    @CrossOrigin(origins = {"${cors-origin}"})
    @PostMapping("/rejestracja-wplaty")
    public String rejestrujWplate(@RequestBody WplataDto wplataDto) {
        Amount kwota = new Amount(wplataDto.getKwota());
        LocalDate dataWplaty = wplataDto.getDataWplaty();

        gate.dispatch(new RegisterPaymentCommand(wplataDto.getIdUmowy(), kwota, dataWplaty));
        return "redirect:/lista-polis";
    }

}