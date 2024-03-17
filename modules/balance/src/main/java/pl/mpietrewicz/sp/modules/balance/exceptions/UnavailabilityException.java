package pl.mpietrewicz.sp.modules.balance.exceptions;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

public class UnavailabilityException extends BalanceException {

    @Getter
    private final AggregateId contractId;

    public UnavailabilityException(AggregateId contractId, Throwable cause, String message, Object... args) {
        super(cause, message, args);
        this.contractId = contractId;
    }

}