package pl.mpietrewicz.sp.modules.finance.ddd.support.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 
 * @author Slawek
 * 
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    private Long entityId;

    public Long getEntityId() {
        return entityId;
    }
}