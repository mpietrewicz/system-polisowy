package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;

public interface LastMonth {

    Amount pay(PositiveAmount payment);

    Amount refund(PositiveAmount refund);

    Amount refund();

    boolean canPaidBy(Amount payment);

    boolean isUnpaid();

    LastMonth createNextMonth(PremiumSnapshot premiumSnapshot);

}