package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.PremiumDue;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@ValueObject
@Entity
public class NewPeriodSnapshot extends BaseEntity {

    private LocalDateTime from;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "premium_due_id")
    private PremiumDue premiumDue;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "period_state_id")
    private PeriodState periodState;

    private Long changeId; // todo: w przyszłości zapisywać co spowodowalo tę zmianę (wpłata, czy zmiana składki po DSK / PSU)

    public NewPeriodSnapshot() {
    }

}