package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.PremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Amount;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.exceptions.NoMonthsToRefundException;

import java.time.YearMonth;

public interface LastMonth {

    Amount pay(PositiveAmount payment);

    Amount refund(PositiveAmount refund) throws NoMonthsToRefundException;

    Amount refund();

    boolean canPaidBy(PositiveAmount payment);

    boolean isUnpaid();

    LastMonth createNextMonth(PremiumSnapshot premiumSnapshot);

    YearMonth getYearMonth();

}