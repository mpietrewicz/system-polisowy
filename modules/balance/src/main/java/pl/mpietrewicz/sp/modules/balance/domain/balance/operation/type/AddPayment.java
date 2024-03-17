package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.AddPaymentFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.MonthToPay;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicyFactory;
import pl.mpietrewicz.sp.modules.balance.exceptions.BalanceException;
import pl.mpietrewicz.sp.modules.balance.exceptions.PaymentException;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_PAYMENT;

@Getter
public class AddPayment extends Operation {

    private static final OperationType operationType = ADD_PAYMENT;

    @Inject
    private PremiumService premiumService;

    private final AggregateId paymentId;

    private final Amount amount;

    private final PaymentPolicyEnum paymentPolicyEnum;

    public AddPayment(AggregateId paymentId, LocalDate date, Amount amount, PaymentPolicyEnum paymentPolicyEnum,
                      DomainEventPublisher eventPublisher, PremiumService premiumService) {
        super(date, eventPublisher);
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentPolicyEnum = paymentPolicyEnum;
        this.premiumService = premiumService;
    }

    public AddPayment(Long id, AggregateId paymentId, LocalDate date, LocalDateTime registration, Amount amount,
                      PaymentPolicyEnum paymentPolicyEnum, List<Period> periods) {
        super(id, date, registration, periods);
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentPolicyEnum = paymentPolicyEnum;
    }

    @Override
    public void execute(AggregateId contractId) {
        try {
            tryExecute(contractId, getRegistration());
        } catch (PaymentException e) {
            handle(contractId, e);
        }
    }

    @Override
    protected void reexecute(AggregateId contractId, LocalDateTime registration) throws ReexecutionException {
        try {
            tryExecute(contractId, registration);
        } catch (PaymentException e) {
            throw new ReexecutionException(e, "Add payment: {} failed!", contractId.getId());
        }
    }

    @Override
    protected void publishFailedEvent(AggregateId contractId, BalanceException e) {
        publish(new AddPaymentFailedEvent(paymentId, date, amount, e));
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    private void tryExecute(AggregateId contractId, LocalDateTime registration) throws PaymentException {
        PaymentData paymentData = new PaymentData(date, amount);

        PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, registration);
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.create(paymentPolicyEnum, premiumSnapshot);

        MonthToPay monthToPay = paymentPolicy.getMonthToPay(getPeriod(), paymentData);

        getPeriod().tryPay(monthToPay, premiumSnapshot);
    }

}