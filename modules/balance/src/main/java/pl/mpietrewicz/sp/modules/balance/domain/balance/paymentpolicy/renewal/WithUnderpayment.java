package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.renewal;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.PaymentPolicy;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy.RenewalPolicy;

@DomainPolicyImpl
public class WithUnderpayment extends RenewalPolicy {

    public WithUnderpayment(PaymentPolicy continuationPolicy, PremiumSnapshot premiumSnapshot, int graceMonths) {
        super(continuationPolicy, premiumSnapshot, graceMonths);
    }

    @Override
    protected Amount getAmountToRenew(Period period, PaymentData paymentData) {
        Amount underpayment = period.refundUnderpayment();
        return paymentData.getAmount().add(underpayment);
    }

}