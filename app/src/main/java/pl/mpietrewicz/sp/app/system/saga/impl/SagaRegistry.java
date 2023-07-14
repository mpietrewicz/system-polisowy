
package pl.mpietrewicz.sp.app.system.saga.impl;

import pl.mpietrewicz.sp.app.system.saga.SagaInstance;
import pl.mpietrewicz.sp.app.system.saga.SagaManager;

import java.util.Collection;

public interface SagaRegistry {

    @SuppressWarnings("rawtypes")
	Collection<SagaManager> getLoadersForEvent(Object event);

    @SuppressWarnings("rawtypes")
    SagaInstance createSagaInstance(Class<? extends SagaInstance> sagaType);
}