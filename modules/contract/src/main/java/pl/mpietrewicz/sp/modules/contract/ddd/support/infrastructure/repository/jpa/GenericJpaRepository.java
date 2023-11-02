
package pl.mpietrewicz.sp.modules.contract.ddd.support.infrastructure.repository.jpa;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.ddd.support.domain.BaseAggregateRoot;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;

/**
 * 
 * @author Slawek
 */
public abstract class GenericJpaRepository<A extends BaseAggregateRoot> {

    @PersistenceContext(unitName = "contract")
    protected EntityManager entityManager;

    private Class<A> clazz;
    
    @Inject
    private AutowireCapableBeanFactory spring;

    @SuppressWarnings("unchecked")
    public GenericJpaRepository() {
        this.clazz = ((Class<A>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public A load(AggregateId id) {
    	//lock to be sure when creating other objects based on values of this aggregate
        A aggregate = entityManager.find(clazz, id, LockModeType.OPTIMISTIC);
        
        if (aggregate == null)
        	throw new RuntimeException("Aggregate " + clazz.getCanonicalName() + " id = " + id + " does not exist");
        
        if (aggregate.isRemoved())
        	throw new RuntimeException("Aggragate + " + id + " is removed.");
        
        spring.autowireBean(aggregate);
        
        return aggregate;
    }

    public void save(A aggregate) {
    	if (entityManager.contains(aggregate)){
    		//locking Aggregate Root logically protects whole aggregate
    		entityManager.lock(aggregate, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    	}
    	else{
    	    entityManager.persist(aggregate);
    	}
    }
    
    public void delete(AggregateId id){
		A entity = load(id);
		//just flag
		entity.markAsRemoved();					
	}
}