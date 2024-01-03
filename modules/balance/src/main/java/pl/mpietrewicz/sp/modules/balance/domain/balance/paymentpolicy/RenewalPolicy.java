package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Premium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static pl.mpietrewicz.sp.ddd.sharedkernel.Amount.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNPAID;

@DomainPolicyImpl
@RequiredArgsConstructor
public class RenewalPolicy implements PaymentPolicy {

    private final PaymentPolicy continuationPolicy;
    private final Premium premium;

    @Override
    public Month getMonthToPay(Period period, PaymentData paymentData) {
        Optional<Month> monthOfPayment = period.getMonthOf(paymentData.getDate());

        if (monthOfPayment.isPresent()) {
            return continuationPolicy.getMonthToPay(period, paymentData);
        } else {
            return tryCreateRenewalMonth(period, paymentData);
        }
    }

    private Month tryCreateRenewalMonth(Period period, PaymentData paymentData) { //
        Month renewalMonth = createRenewalMonth(paymentData.getDate(), premium);
        Amount payment = paymentData.getAmount();

        if (renewalMonth.canPaidBy(payment)) {
            period.addNewMonth(renewalMonth);
            return renewalMonth;
        } else {
            throw new RuntimeException("Wpłata i tak nie starczy na pokrycie pierwszego miesiąca");
            // todo: wysłać zdarzenie, że wpłata nie może wznowić umowy! // a co jeśli wcześniej już wznawiała?
        }
    }

    public Month createRenewalMonth(LocalDate date, Premium premium) {
        return new Month(YearMonth.from(date), UNPAID, ZERO, ZERO, premium.getComponentPremiums());
    }

}