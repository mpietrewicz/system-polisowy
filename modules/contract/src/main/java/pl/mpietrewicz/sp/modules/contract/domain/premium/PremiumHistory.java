package pl.mpietrewicz.sp.modules.contract.domain.premium;

import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Embeddable
@Entity
public class PremiumHistory extends BaseEntity {

    private LocalDate since;
    private LocalDateTime registration;
    private BigDecimal monthlyAmount;

    public PremiumHistory() {
    }

    public PremiumHistory(LocalDate since, BigDecimal amount) {
        this.since = since;
        this.registration = LocalDateTime.now();
        this.monthlyAmount = amount;
    }

    protected BigDecimal getMonthlyAmount() {
        return monthlyAmount;
    }

    protected LocalDate getSince() {
        return since;
    }

    protected int premiumDateComparator(PremiumHistory premium) {
        int sinceComparatorResult = sinceComparator(premium.since);
        if (sinceComparatorResult != 0) {
            return sinceComparatorResult;
        } else {
            return registrationComparator(premium.registration);
        }
    }

    private int registrationComparator(LocalDateTime dateTime) {
        return this.registration.compareTo(dateTime);
    }

    private int sinceComparator(LocalDate date) {
        return this.since.compareTo(date);
    }

}