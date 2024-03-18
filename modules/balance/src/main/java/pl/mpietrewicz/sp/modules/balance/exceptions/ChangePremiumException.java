package pl.mpietrewicz.sp.modules.balance.exceptions;

import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;

public class ChangePremiumException extends BalanceException {

    @Getter
    private final PremiumSnapshot premiumSnapshot;

    private final PaymentException exception;

    public ChangePremiumException(PremiumSnapshot premiumSnapshot, PaymentException exception, String message) {
        super(message);
        this.premiumSnapshot = premiumSnapshot;
        this.exception = exception;
    }

}