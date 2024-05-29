package pl.mpietrewicz.sp.modules.finance.webui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.mpietrewicz.sp.cqrs.command.Gate;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.finance.application.commands.RegisterPaymentCommand;
import pl.mpietrewicz.sp.modules.finance.readmodel.FinanceFinder;
import pl.mpietrewicz.sp.modules.finance.readmodel.model.Payment;
import pl.mpietrewicz.sp.modules.finance.readmodel.model.RegisterPayment;

import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
@CrossOrigin(origins = {"${cors-origin}"})
@RestController
public class FinanceController {

    private final FinanceFinder financeFinder;

    private final Gate gate;

    @GetMapping("contract/{contractId}/payment")
    public List<Payment> getPayments(@PathVariable String contractId) {
        return financeFinder.find(new AggregateId(contractId));
    }

    @GetMapping("contract/{contractId}/payment/{paymentId}")
    public Payment getPayment(@PathVariable String contractId, @PathVariable String paymentId) {
        return financeFinder.find(new AggregateId(contractId), new AggregateId(paymentId));
    }

    @PostMapping("contract/{contractId}/payment/register")
    public void registerPayment(@PathVariable String contractId, @RequestBody RegisterPayment registerPayment)
            throws NotPositiveAmountException {
        PositiveAmount payment = PositiveAmount.withValue(registerPayment.getAmount());
        LocalDate date = registerPayment.getDate();

        gate.dispatch(new RegisterPaymentCommand(new AggregateId(contractId), payment, date));
    }

}