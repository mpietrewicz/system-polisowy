package pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy;

import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.Periods;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.operation.Refund;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.period.Period;
import pl.mpietrewicz.sp.modules.oldbalance.domain.balance.policy.periodcover.PeriodCoverPolicyEnum;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.support.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@ValueObject
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "policyName", discriminatorType = DiscriminatorType.STRING)
public abstract class PeriodCoverPolicy extends BaseEntity {

    @Column(insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private PeriodCoverPolicyEnum policyName;

    public PeriodCoverPolicy() {
    }

    public abstract Period getFirstPeriodToPay(Periods periods, Operation operation);

    public abstract Period getLastPeriodToRefund(Periods periods, Refund refund);

}