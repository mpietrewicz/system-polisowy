package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.ChangePremiumFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.MonthToPay;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.ContinuationPolicy;
import pl.mpietrewicz.sp.modules.balance.exceptions.BalanceException;
import pl.mpietrewicz.sp.modules.balance.exceptions.PaymentException;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.contract.application.api.PremiumService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CHANGE_PREMIUM;

@Getter
public class ChangePremium extends Operation {

    private static final OperationType operationType = CHANGE_PREMIUM;

    @Inject
    protected PremiumService premiumService;

    public ChangePremium(LocalDate date, LocalDateTime timestamp, DomainEventPublisher eventPublisher,
                         PremiumService premiumService) {
        super(date, timestamp, eventPublisher);
        this.premiumService = premiumService;
    }

    public ChangePremium(Long id, LocalDate date, LocalDateTime registration, List<Period> periods) {
        super(id, date, registration, periods);
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
            throw new ReexecutionException(e, "Change premium on contract ({}) balance failed during reexecution!",
                    contractId.getId());
        }
    }

    @Override
    protected void publishFailedEvent(AggregateId contractId, BalanceException e) {
        ChangePremiumFailedEvent event = new ChangePremiumFailedEvent(contractId, getRegistration(), e);
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    private void tryExecute(AggregateId contractId, LocalDateTime registration) throws PaymentException {
        YearMonth monthOfChange = YearMonth.from(date);
        Amount refunded = getPeriod().tryRefundUpTo(monthOfChange);

        if (refunded.isPositive()) {
            PremiumSnapshot premiumSnapshot = premiumService.getPremiumSnapshot(contractId, registration);

            ContinuationPolicy continuationPolicy = new ContinuationPolicy(premiumSnapshot);
            PaymentData paymentData = new PaymentData(date, refunded);
            MonthToPay monthToPay = continuationPolicy.getMonthToPay(getPeriod(), paymentData);

            getPeriod().tryPay(monthToPay, premiumSnapshot);
        }
    }

}