package pl.mpietrewicz.sp.modules.finance.domain.refund;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.RefundData;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

import java.time.LocalDate;

@DomainFactory
public class RefundFactory {

    public Refund create(RefundData refundData) {
        AggregateId contractId = refundData.getContractId();
        AggregateId refundId = refundData.getAggregateId();
        PositiveAmount refund = refundData.getRefund();
        LocalDate date = refundData.getDate();

        return new Refund(refundId, contractId, refund, date);
    }

}