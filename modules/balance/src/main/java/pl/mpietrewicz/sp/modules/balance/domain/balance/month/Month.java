package pl.mpietrewicz.sp.modules.balance.domain.balance.month;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.PositiveAmount;
import pl.mpietrewicz.sp.modules.balance.ddd.support.domain.BaseEntity;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@DomainEntity
@Entity
@NoArgsConstructor
public class Month extends BaseEntity {

    private YearMonth yearMonth;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<ComponentPremium> componentPremiums = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "month_state_id")
    private MonthState monthState;

    public Month(YearMonth yearMonth, MonthStatus monthStatus, Amount underpayment, Amount overpayment,
                 List<ComponentPremium> componentPremiums) {
        this.yearMonth = yearMonth;
        this.monthState = MonthStateFactory.createState(this, monthStatus, underpayment, overpayment);
        this.componentPremiums = componentPremiums;
    }

    public Month(YearMonth yearMonth, MonthState monthState, List<ComponentPremium> componentPremiums) {
        this.yearMonth = yearMonth;
        this.monthState = monthState;
        this.componentPremiums = componentPremiums;
    }

    public Month createNextMonth(List<ComponentPremium> componentPremiums) {
        return monthState.createNextMonth(componentPremiums);
    }

    public Amount pay(PositiveAmount payment, Optional<Month> nextMonth) {
        return monthState.pay(payment, nextMonth);
    }

    public Amount refund(PositiveAmount refund, Optional<Month> previousMonth) {
        return monthState.refund(refund, previousMonth);
    }

    public Amount refund() {
        return monthState.refund();
    }

    public Amount getPaidAmount() {
        return monthState.getPaid();
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public Amount getPremium() {
        return componentPremiums.stream()
                .map(ComponentPremium::getPremium)
                .reduce(Amount.ZERO, Amount::add);
    }

    public Map<AggregateId, Amount> getPremiumComponents() {
        return componentPremiums.stream()
                .collect(Collectors.toMap(
                        ComponentPremium::getComponentId,
                        ComponentPremium::getPremium));
    }

    public Month createCopy() { // todo: tak średnio to wygląda
        Month monthCopy = new Month(yearMonth, monthState, getComponentPremiums());
        MonthState monthStateCopy = MonthStateFactory.createState(monthCopy, monthState.getStatus(),
                monthState.underpayment, monthState.underpayment);
        monthCopy.changeState(monthStateCopy);
        return monthCopy;
    }

    public void changeState(MonthState monthState) {
        this.monthState = monthState;
    }

    public boolean isPaid() {
        return monthState.isPaid();
    }

    public boolean isPartlyPaid() {
        return monthState.isPaid();
    }

    public boolean isNotPaid() {
        return monthState.isNotPaid();
    }

    public boolean canBeDeleted() {
        return getPaidAmount() == Amount.ZERO;
    }

    public int compareAscending(Month month) {
        return this.yearMonth.compareTo(month.getYearMonth());
    }

    public int compareDescending(Month month) {
        return month.getYearMonth().compareTo(this.yearMonth);
    }

    private List<ComponentPremium> getComponentPremiums() {
        return new ArrayList<>(componentPremiums);
    }

    @Override
    public String toString() {
        return yearMonth +
                ", " + getPremium() +
                ", " + monthState.getStatus();
    }

    public boolean isAfter(Month month) {
        return compareAscending(month) > 0;
    }

    public boolean isBefore(Month month) {
        return compareAscending(month) < 0;
    }

    public boolean equals(Month month) {
        return compareAscending(month) == 0;
    }

    public boolean canPaidBy(Amount payment) {
        return monthState.canPaidBy(payment);
    }
}