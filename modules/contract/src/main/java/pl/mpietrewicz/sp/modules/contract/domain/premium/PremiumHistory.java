package pl.mpietrewicz.sp.modules.contract.domain.premium;

import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.contract.ddd.support.domain.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Embeddable
@Entity
public class PremiumHistory extends BaseEntity {

    private LocalDate since;

    private LocalDateTime registration;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "premium"))
    private Amount premium;

    public PremiumHistory() {
    }

    public PremiumHistory(LocalDate since, Amount amount) {
        this.since = since;
        this.registration = LocalDateTime.now();
        this.premium = amount;
    }

    protected Amount getPremium() {
        return premium;
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