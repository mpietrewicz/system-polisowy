
package pl.mpietrewicz.sp.ddd.support.infrastructure.repo;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.lang.reflect.ParameterizedType;

/**
 *
 * @author Slawek
 */
public abstract class GenericJpaRepository<A extends BaseAggregateRoot> {

    private Class<A> clazz;

    @SuppressWarnings("unchecked")
    public GenericJpaRepository() {
        this.clazz = ((Class<A>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public abstract EntityManager getEntityManager();

    @Transactional(propagation = Propagation.SUPPORTS)
    public A load(AggregateId id) {
        // lock to be sure when creating other objects based on values of this aggregate
        return getEntityManager().find(clazz, id, LockModeType.OPTIMISTIC);
    }

    public void save(A aggregate) {
        EntityManager entityManager = getEntityManager();

        if (entityManager.contains(aggregate)) {
            // locking Aggregate Root logically protects whole aggregate
            entityManager.lock(aggregate, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        } else {
            entityManager.persist(aggregate);
        }
    }
    
    public void delete(AggregateId id){
		A entity = load(id);
		// just flag
		entity.markAsRemoved();					
	}

}