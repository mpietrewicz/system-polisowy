package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.events.AddPaymentFailedEvent;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.modules.balance.domain.balance.MonthToPay;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicyFactory;
import pl.mpietrewicz.sp.modules.balance.exceptions.ReexecutionException;
import pl.mpietrewicz.sp.modules.balance.exceptions.RenewalException;

import java.time.LocalDate;
import java.util.List;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_PAYMENT;

@Getter
public class AddPayment extends Operation {

    private final Amount amount;

    private final PaymentPolicyEnum paymentPolicyEnum;

    public AddPayment(LocalDate date, Amount amount, PaymentPolicyEnum paymentPolicyEnum) {
        super(date);
        this.amount = amount;
        this.paymentPolicyEnum = paymentPolicyEnum;
        this.type = ADD_PAYMENT;
    }

    public AddPayment(Long id, LocalDate date, Amount amount, PaymentPolicyEnum paymentPolicyEnum, List<Period> periods) {
        super(id, date, periods);
        this.amount = amount;
        this.paymentPolicyEnum = paymentPolicyEnum;
        this.type = ADD_PAYMENT;
    }

    @Override
    public void execute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher) {
        try {
            tryExecute(premiumSnapshot);
        } catch (RenewalException e) {
            handle(e, eventPublisher);
        }
    }

    @Override
    protected void reexecute(PremiumSnapshot premiumSnapshot, DomainEventPublisher eventPublisher)
            throws ReexecutionException {
        try {
            tryExecute(premiumSnapshot);
        } catch (RenewalException e) {
            throw new ReexecutionException("Add payment failed!", e);
        }
    }

    @Override
    protected void publishFailedEvent(Exception e, DomainEventPublisher eventPublisher) {
        AddPaymentFailedEvent event = new AddPaymentFailedEvent(date, amount, e);
        eventPublisher.publish(event, "BalanceServiceImpl");
    }

    private void tryExecute(PremiumSnapshot premiumSnapshot) throws RenewalException {
        PaymentData paymentData = new PaymentData(date, amount);
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.create(paymentPolicyEnum, premiumSnapshot);

        MonthToPay monthToPay = paymentPolicy.getMonthToPay(getPeriod(), paymentData);

        getPeriod().tryPay(monthToPay, premiumSnapshot);
    }

}