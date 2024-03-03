package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseAggregateRoot;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Table(name = "Balance")
@AggregateRoot
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceEntity extends BaseAggregateRoot {

    @Embedded
    @AttributeOverride(name = "aggregateId", column = @Column(name = "contractId", nullable = false))
    private AggregateId contractId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id")
    private List<OperationEntity> operations;

    public BalanceEntity(AggregateId aggregateId, Long version, AggregateId contractId, List<OperationEntity> operations) {
        this.version = version;
        this.aggregateId = aggregateId;
        this.contractId = contractId;
        this.operations = operations;
    }

}