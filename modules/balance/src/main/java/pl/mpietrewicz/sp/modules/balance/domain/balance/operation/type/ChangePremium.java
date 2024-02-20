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
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.ContinuationPolicy;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CHANGE_PREMIUM;

@Getter
public class ChangePremium extends Operation {

    private final Amount premium;

    private final AggregateId premiumId;

    private final LocalDateTime timestamp;

    public ChangePremium(LocalDate date, Amount premium, AggregateId premiumId, LocalDateTime timestamp) {
        super(date);
        this.premium = premium;
        this.type = CHANGE_PREMIUM;
        this.premiumId = premiumId;
        this.timestamp = timestamp;
    }

    public ChangePremium(Long id, LocalDate date, Amount premium, AggregateId premiumId, LocalDateTime timestamp, List<Period> periods) {
        super(id, date, periods);
        this.premium = premium;
        this.type = CHANGE_PREMIUM;
        this.premiumId = premiumId;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        YearMonth monthOfChange = YearMonth.from(date);
        Amount refunded = getPeriod().tryRefundUpTo(monthOfChange);

        if (refunded.isPositive()) {
            ContinuationPolicy continuationPolicy = new ContinuationPolicy(premiumSnapshot);
            PaymentData paymentData = new PaymentData(date, refunded);
            MonthToPay monthToPay = continuationPolicy.getMonthToPay(getPeriod(), paymentData);

            getPeriod().tryPay(monthToPay, premiumSnapshot);
        }
    }

    @Override
    protected void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        execute(premiumSnapshot, eventPublisher);
    }

    @Override
    public void handle(ReexecutionException e, DomainEventPublisher eventPublisher) {
        publishFailedEvent(e, eventPublisher);
    }

    protected void execute() {
        throw new UnsupportedOperationException("Metoda nie obsługiwana w StartCalculating Operation");
    }

    private void publishFailedEvent(Exception e, DomainEventPublisher eventPublisher) {
        ChangePremiumFailedEvent event = new ChangePremiumFailedEvent(premiumId, timestamp, premium, date, e);
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

}