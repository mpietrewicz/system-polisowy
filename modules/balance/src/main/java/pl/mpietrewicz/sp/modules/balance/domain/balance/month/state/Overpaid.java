package pl.mpietrewicz.sp.modules.balance.domain.balance.month.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.balance.domain.balance.AccountingMonth;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.ComponentPremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthState;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.OVERPAID;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.PAID;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.MonthStatus.UNDERPAID;

@Entity
@NoArgsConstructor
public class Overpaid extends MonthState {

    public Overpaid(Month month, BigDecimal overpayment) {
        super(month, OVERPAID, ZERO, overpayment);
    }

    public void pay(BigDecimal payment) {
        if (month.getNext().isPresent()) {
            throw new RuntimeException("The overpaid month is not last!");
        } else {
            increaseOverpayment(payment);
        }
    }

    public void refund(BigDecimal refund) {
        BigDecimal overpayment = getOverpayment();
        BigDecimal paid = month.getPremium().add(overpayment);

        if (refund.compareTo(paid) > 0) {
            if (month.getPrevious().isPresent()) {
                month.changeState(new Unpaid(month));
                month.getPrevious().get().tryRefund(refund.subtract(paid));
            } else {
                throw new RuntimeException("You're trying to refund more than you have!");
            }
        } else if (refund.compareTo(paid) == 0) {
            month.changeState(new Unpaid(month));
        } else if (refund.compareTo(paid) < 0) {

            if (refund.compareTo(overpayment) > 0) {
                month.changeState(new Underpaid(month, refund.subtract(overpayment)));
            } else if (refund.compareTo(overpayment) == 0) {
                month.changeState(new Paid(month));
            } else if (refund.compareTo(overpayment) < 0 ) {
                decreaseOverpayment(refund);
            }

        }
    }

    @Override
    public Month createNextMonth(AccountingMonth accountingMonth, List<ComponentPremium> componentPremiums) {
        BigDecimal premium = componentPremiums.stream().map(ComponentPremium::getAmount).reduce(ZERO, BigDecimal::add);
        Month previous = this.month;

        BigDecimal overpayment = getOverpayment();

        previous.changeState(new Paid(month));

        if (overpayment.compareTo(premium) > 0) {
            return createOverpaid(accountingMonth, overpayment.subtract(premium), previous, componentPremiums);
        } else if (overpayment.compareTo(premium) == 0) {
            return createPaid(accountingMonth, previous, componentPremiums);
        } else {
            return createUnderpaid(accountingMonth, premium.subtract(overpayment), previous, componentPremiums);
        }
    }

    @Override
    public BigDecimal getPaid() {
        return getOverpayment();
    }

    private Month createOverpaid(AccountingMonth accountingMonth, BigDecimal overpayment, Month previous,
                                 List<ComponentPremium> componentPremiums) {
        return new Month(previous.getYearMonth().plusMonths(1), accountingMonth,
                OVERPAID, ZERO, overpayment, previous, componentPremiums);
    }

    private Month createPaid(AccountingMonth accountingMonth, Month previous, List<ComponentPremium> componentPremiums) {
        return new Month(previous.getYearMonth().plusMonths(1), accountingMonth,
                PAID, ZERO, ZERO, previous, componentPremiums);
    }

    private Month createUnderpaid(AccountingMonth accountingMonth, BigDecimal underpayment, Month previous,
                                  List<ComponentPremium> componentPremiums) {
        return new Month(previous.getYearMonth().plusMonths(1), accountingMonth,
                UNDERPAID, underpayment, ZERO, previous, componentPremiums);
    }

}