package pl.mpietrewicz.sp.modules.balance.domain.balance.paymentpolicy;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.PaymentData;

import java.time.YearMonth;

@DomainPolicyImpl
@RequiredArgsConstructor
public class NoRenewalPolicy implements PaymentPolicy {

    private final PaymentPolicy continuationPolicy;

    @Override
    public Month getMonthToPay(Period period, PaymentData paymentData) {
        if (period.getMonthOf(YearMonth.from(paymentData.getDate())).isPresent()) {
            return continuationPolicy.getMonthToPay(period, paymentData);
        } else {
            throw new RuntimeException("Nie można wzowić umowy"); // todo: co jeśli wcześniej wpłata nie rzucała tego wyjątku?
        }
    }

}