package pl.mpietrewicz.sp.modules.contract.webui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.mpietrewicz.sp.cqrs.command.Gate;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.contract.application.commands.AddComponentCommand;
import pl.mpietrewicz.sp.modules.contract.application.commands.ChangePremiumCommand;
import pl.mpietrewicz.sp.modules.contract.application.commands.RegisterContractCommand;
import pl.mpietrewicz.sp.modules.contract.application.commands.TerminateComponentCommand;
import pl.mpietrewicz.sp.modules.contract.readmodel.ComponentFinder;
import pl.mpietrewicz.sp.modules.contract.readmodel.ContractFinder;
import pl.mpietrewicz.sp.modules.contract.readmodel.PremiumFinder;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.AddComponent;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.ChangePremium;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.Component;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.Contract;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.Premium;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.RegisterContract;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.TerminateComponent;

import java.time.LocalDate;
import java.util.List;

@Getter
@CrossOrigin(origins = {"${cors-origin}"})
@RestController
@RequiredArgsConstructor
public class ContractController {

    private final ContractFinder contractFinder;

    private final ComponentFinder componentFinder;

    private final PremiumFinder premiumFinder;

    private final Gate gate;

    @GetMapping("/contract")
    public List<Contract> getContracts() {
        return contractFinder.find();
    }

    @GetMapping("/contract/{contractId}")
    public Contract getContract(@PathVariable String contractId) {
        return contractFinder.find(new AggregateId(contractId))
                .orElseThrow(() -> new ContractNotFoundException("Contract not found with id: " + contractId));
    }

    @GetMapping("/contract/{contractId}/component")
    public List<Component> getContractComponents(@PathVariable String contractId) {
        return componentFinder.find(new AggregateId(contractId));
    }

    @GetMapping("/contract/{contractId}/component/{componentId}")
    public Component getContractComponent(@PathVariable String contractId, @PathVariable String componentId) {
        return componentFinder.find(new AggregateId(contractId), new AggregateId(componentId))
                .orElseThrow(() -> new ComponentNotFoundException("Component not found with id: " + contractId));
    }

    @GetMapping("/contract/{contractId}/premium")
    public Premium getPremium(@PathVariable String contractId) {
        return premiumFinder.find(new AggregateId(contractId));
    }

    @GetMapping("/contract/{contractId}/component/{componentId}/premium")
    public Premium getPremium(@PathVariable String contractId, @PathVariable String componentId) {
        return premiumFinder.find(new AggregateId(contractId), new AggregateId(componentId));
    }

    @PostMapping("/contract/register")
    public void registerContract(@RequestBody RegisterContract registerContract) throws NotPositiveAmountException {
        String department = registerContract.getDepartment();
        String name = registerContract.getName();
        LocalDate start = registerContract.getStart();
        PositiveAmount premium = PositiveAmount.withValue(registerContract.getPremium());
        Frequency frequency = registerContract.getFrequency();

        gate.dispatch(new RegisterContractCommand(department, name, start, premium, frequency));
    }

    @PostMapping("/contract/{contractId}/component/{componentId}/premium/change")
    public void changePremium(@PathVariable String componentId, @RequestBody ChangePremium changePremium)
            throws NotPositiveAmountException {
        LocalDate start = changePremium.getStart();
        PositiveAmount premium = PositiveAmount.withValue(changePremium.getAmount());

        gate.dispatch(new ChangePremiumCommand(new AggregateId(componentId), premium, start));
    }

    @PostMapping("/contract/{contractId}/component/add")
    public void addComponent(@PathVariable String contractId, @RequestBody AddComponent addComponent) throws NotPositiveAmountException {
        String name = addComponent.getName();
        LocalDate start = addComponent.getStart();
        PositiveAmount premium = PositiveAmount.withValue(addComponent.getPremium());

        gate.dispatch(new AddComponentCommand(new AggregateId(contractId), name, start, premium));
    }

    @PostMapping("/contract/{contractId}/component/{componentId}/terminate")
    public void terminateComponent(@PathVariable String componentId, @RequestBody TerminateComponent terminateComponent) {
        LocalDate terminatedDate = terminateComponent.getTerminatedDate();

        gate.dispatch(new TerminateComponentCommand(new AggregateId(componentId), terminatedDate));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ContractNotFoundException extends RuntimeException {
        public ContractNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ComponentNotFoundException extends RuntimeException {
        public ComponentNotFoundException(String message) {
            super(message);
        }
    }

}