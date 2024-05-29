package pl.mpietrewicz.sp.modules.accounting.domain.allocation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

@ValueObject
@Entity
@NoArgsConstructor
public class Risk extends BaseEntity {

    @Getter
    private Long riskId;

    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "riskPremium"))
    private PositiveAmount riskPremium;

    public Risk(Long riskId, String name, PositiveAmount riskPremium) {
        this.riskId = riskId;
        this.name = name;
        this.riskPremium = riskPremium;
    }

}