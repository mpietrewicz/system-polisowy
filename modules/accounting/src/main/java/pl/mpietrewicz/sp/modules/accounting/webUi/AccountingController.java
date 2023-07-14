package pl.mpietrewicz.sp.modules.accounting.webUi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mpietrewicz.sp.cqrs.command.Gate;
import pl.mpietrewicz.sp.modules.accounting.application.commands.ChangeMonthCommand;

@Getter
@CrossOrigin(origins = {"${cors-origin}"})
@RestController
@RequiredArgsConstructor
public class AccountingController {

    private final Gate gate;

    @CrossOrigin(origins = {"${cors-origin}"})
    @GetMapping("/otwarcie-miesiaca")
    public String otworzMiesiac() {
        gate.dispatch(new ChangeMonthCommand());
        return "redirect:/lista-polis";
    }

}