package pl.mpietrewicz.sp.modules.contract.domain.premium;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.premium.ChangePremiumSnapshot;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Embeddable
@Entity
public class Operation extends BaseEntity {

    private LocalDateTime registration;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Embedded
    @AttributeOverride(name = "registration", column = @Column(name = "cancelation"))
    private Cancelation cancelation;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Amount amount;

    public Operation() {
    }

    public Operation(Type type, LocalDate date, Amount amount, LocalDateTime timestamp) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.registration = timestamp;
    }

    public void cancel() {
        if (cancelation == null) {
            cancelation = new Cancelation();
        } else {
            throw new IllegalStateException();
        }
    }

    protected boolean registeredBefore(LocalDateTime timestamp) {
        return registration.compareTo(timestamp) <= 0;
    }

    public boolean isCanceled(LocalDateTime timestamp) {
        return cancelation != null && cancelation.isHappenedBefore(timestamp);
    }

    public Type getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public Amount getAmount() {
        return amount;
    }

    public ChangePremiumSnapshot getChangePremiumSnapshot() {
        return ChangePremiumSnapshot.builder()
                .date(date)
                .amount(amount)
                .build();
    }

}