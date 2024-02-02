package pl.mpietrewicz.sp.modules.balance.ddd.support.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entityId;

    public Long getEntityId() {
        return entityId;
    }
}