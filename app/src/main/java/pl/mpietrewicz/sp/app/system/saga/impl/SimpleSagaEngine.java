
package pl.mpietrewicz.sp.app.system.saga.impl;

import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.app.system.infrastructure.events.SimpleEventPublisher;
import pl.mpietrewicz.sp.app.system.infrastructure.events.impl.handlers.EventHandler;
import pl.mpietrewicz.sp.app.system.saga.SagaEngine;
import pl.mpietrewicz.sp.app.system.saga.SagaInstance;
import pl.mpietrewicz.sp.app.system.saga.SagaManager;
import pl.mpietrewicz.sp.app.system.saga.annotations.LoadSaga;
import pl.mpietrewicz.sp.app.system.saga.annotations.SagaAction;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Rafał Jamróz
 */
@Component
public class SimpleSagaEngine implements SagaEngine {

    private final SagaRegistry sagaRegistry;

    private final SimpleEventPublisher eventPublisher;

    @Inject
    public SimpleSagaEngine(SagaRegistry sagaRegistry, SimpleEventPublisher eventPublisher) {
        this.sagaRegistry = sagaRegistry;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void registerEventHandler() {
        eventPublisher.registerEventHandler(new SagaEventHandler(this));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void handleSagasEvent(Object event) {
        Collection<SagaManager> loaders = sagaRegistry.getLoadersForEvent(event);
        for (SagaManager loader : loaders) {
            SagaInstance sagaInstance = loadSaga(loader, event);
            invokeSagaActionForEvent(sagaInstance, event);
            if (sagaInstance.isCompleted()) {
                loader.removeSaga(sagaInstance);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private SagaInstance loadSaga(SagaManager loader, Object event) {
        Class<? extends SagaInstance> sagaType = determineSagaTypeByLoader(loader);
        Object sagaData = loadSagaData(loader, event);
        if (sagaData == null) {
            sagaData = loader.createNewSagaData();
        }
        SagaInstance sagaInstance = sagaRegistry.createSagaInstance(sagaType);
        sagaInstance.setData(sagaData);
        return sagaInstance;
    }

    // TODO determine saga type more reliably
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private Class<? extends SagaInstance> determineSagaTypeByLoader(SagaManager loader) {
        Type type = ((ParameterizedType) loader.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        return ((Class<? extends SagaInstance>) type);
    }

    /**
     * TODO handle exception in more generic way
     */
    @SuppressWarnings("rawtypes")
	private Object loadSagaData(SagaManager loader, Object event) {
        Method loaderMethod = findHandlerMethodForEvent(loader.getClass(), event);
        try {
            Object sagaData = loaderMethod.invoke(loader, event);
            return sagaData;
        } catch (InvocationTargetException e) {
            // NRE is ok here, it means that saga hasn't been started yet
            if (e.getTargetException() instanceof NoResultException) {
                return null;
            } else {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeSagaActionForEvent(SagaInstance<?> saga, Object event) {
        Method eventHandler = findHandlerMethodForEvent(saga.getClass(), event);
        try {
            eventHandler.invoke(saga, event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Method findHandlerMethodForEvent(Class<?> type, Object event) {
        for (Method method : type.getMethods()) {
            if (method.getAnnotation(SagaAction.class) != null || method.getAnnotation(LoadSaga.class) != null) {
                if (method.getParameterTypes().length == 1
                        && method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                    return method;
                }
            }
        }
        throw new RuntimeException("no method handling " + event.getClass());
    }

    private static class SagaEventHandler implements EventHandler {

        private final SagaEngine sagaEngine;

        public SagaEventHandler(SagaEngine sagaEngine) {
            this.sagaEngine = sagaEngine;
        }

        @Override
        public boolean canHandle(Object event) {
            return true;
        }

        @Override
        public void handle(Object event) {
            sagaEngine.handleSagasEvent(event);
        }
    }
}