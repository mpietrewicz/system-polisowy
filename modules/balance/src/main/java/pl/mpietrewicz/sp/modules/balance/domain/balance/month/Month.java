package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ValueObject
@Entity
@NoArgsConstructor
public class Month extends BaseEntity {

    private YearMonth month;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<ComponentPremium> componentPremiums = new ArrayList<>(); // todo: zamienić na hashset

    @OneToOne(cascade = CascadeType.ALL)
    private Month previous;

    @OneToOne(cascade = CascadeType.ALL)
    private Month next;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "month_state_id")
    private MonthState monthState;

    public Month(YearMonth month, MonthStatus monthStatus, BigDecimal underpayment, BigDecimal overpayment,
                 List<ComponentPremium> componentPremiums) {
        this.month = month;
        this.monthState = MonthStateFactory.createState(this, monthStatus, underpayment, overpayment);
        this.componentPremiums = componentPremiums;
    }

    public Month(YearMonth month, MonthStatus monthStatus, BigDecimal underpayment, BigDecimal overpayment,
                 Month previous, List<ComponentPremium> componentPremiums) {
        this.month = month;
        this.monthState = MonthStateFactory.createState(this, monthStatus, underpayment, overpayment);
        this.previous = previous;
        this.componentPremiums = componentPremiums;
    }

    public Month createNextMonth() {
        return monthState.createNextMonth(getComponentPremiums());
    }

    public Month createNextMonth(List<ComponentPremium> componentPremiums) {
        return monthState.createNextMonth(componentPremiums);
    }

    public void tryPay(BigDecimal payment) {
        if (payment.signum() > 0) {
            monthState.pay(payment);
        }
    }

    public void tryRefund(BigDecimal refund) {
        if (refund.signum() > 0) {
            monthState.refund(refund);
        }
    }

    public void invalidate() {
        if (!isTheLastOne()) {
            throw new IllegalStateException("Nie można usuwać oresów innych niż ostatni");
        }
        BigDecimal paid = monthState.getPaid();
        getPrevious().ifPresent(month -> {
            month.setNext(null);
            month.tryPay(paid);
        });
    }

    public YearMonth getYearMonth() {
        return month;
    }

    public BigDecimal getPremium() {
        return componentPremiums.stream()
                .map(ComponentPremium::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<AggregateId, BigDecimal> getPremiumComponents() {
        return componentPremiums.stream()
                .collect(Collectors.toMap(
                        ComponentPremium::getComponentId,
                        ComponentPremium::getAmount));
    }

    public Month createCopy() {
        return new Month(month, monthState.getStatus(), monthState.getUnderpayment(), monthState.getOverpayment(),
                getComponentPremiums());
    }

    public Optional<Month> getPrevious() {
        return Optional.ofNullable(previous);
    }

    public Optional<Month> getNext() {
        return Optional.ofNullable(next);
    }

    public void changeState(MonthState monthState) {
        this.monthState = monthState;
    }

    public void setNext(Month next) {
        this.next = next;
    }

    public void setPervious(Month previous) {
        this.previous = previous;
    }

    public boolean isPaid() {
        return monthState.isPaid();
    }

    public boolean isNotPaid() {
        return monthState.isNotPaid();
    }

    private boolean isTheLastOne() {
        return getNext().isEmpty();
    }

    public int orderComparator(Month month) {
        return this.month.compareTo(month.getYearMonth());
    }

    public List<ComponentPremium> getComponentPremiums() {
        return new ArrayList<>(componentPremiums);
    }

    @Override
    public String toString() {
        return month +
                ", " + getPremium() +
                ", " + monthState.getStatus();
    }
}