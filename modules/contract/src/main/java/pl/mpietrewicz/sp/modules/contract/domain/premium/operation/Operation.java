package pl.mpietrewicz.sp.modules.contract.domain.premium.operation;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.ChangePremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "PremiumOperation")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Operation extends BaseEntity {

    private LocalDateTime registration;

    private LocalDate date;

    @Embedded
    @AttributeOverride(name = "registration", column = @Column(name = "cancelation"))
    private Cancelation cancelation;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private PositiveAmount premium;

    public Operation() {
    }

    public Operation(LocalDate date, PositiveAmount premium, LocalDateTime timestamp) {
        this.date = date;
        this.premium = premium;
        this.registration = timestamp;
    }

    public abstract Type getType();

    public void cancel() {
        if (cancelation == null) {
            cancelation = new Cancelation();
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean registeredBefore(LocalDateTime timestamp) {
        return registration.compareTo(timestamp) <= 0;
    }

    public boolean isCanceled(LocalDateTime timestamp) {
        return cancelation != null && cancelation.isHappenedBefore(timestamp);
    }

    public LocalDate getDate() {
        return date;
    }

    public PositiveAmount getPremium() {
        return premium;
    }

    public ChangePremiumSnapshot getChangePremiumSnapshot() {
        return ChangePremiumSnapshot.builder()
                .date(date)
                .premium(premium)
                .build();
    }

}