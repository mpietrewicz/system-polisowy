package pl.mpietrewicz.sp.modules.finance.readmodel.converter;

import org.springframework.stereotype.Service;
import pl.mpietrewicz.sp.modules.finance.domain.refund.Refund;
import pl.mpietrewicz.sp.modules.finance.readmodel.model.Payment;

@Service
public class PaymentConverter {

    public Payment convert(pl.mpietrewicz.sp.modules.finance.domain.payment.Payment payment) {
        return Payment.builder()
                .date(payment.getDate())
                .registration(payment.getRegistration())
                .amount(payment.getPayment().getValue())
                .build();
    }

    public Payment convert(Refund refund) {
        return Payment.builder()
                .date(refund.getDate())
                .registration(refund.getRegistration())
                .amount(refund.getRefund().getValue().negate())
                .build();
    }

}