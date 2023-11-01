package pl.mpietrewicz.sp.modules.accounting.webUi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mpietrewicz.sp.cqrs.command.Gate;
import pl.mpietrewicz.sp.modules.accounting.application.commands.GenerateInterfaceCommand;

@Getter
@CrossOrigin(origins = {"${cors-origin}"})
@RestController
@RequiredArgsConstructor
public class SubledgerController {

    private final Gate gate;

    @CrossOrigin(origins = {"${cors-origin}"})
    @GetMapping("/generuj-interfejs")
    public String generujInterfejs() {
        gate.dispatch(new GenerateInterfaceCommand());
        return "redirect:/lista-polis";
    }

}