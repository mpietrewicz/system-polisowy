
package pl.mpietrewicz.sp.app.system.infrastructure.events;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.app.system.infrastructure.events.impl.handlers.EventHandler;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Aspect
@Primary
@Component
public class TransactionalAspectEventPublisher implements DomainEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalAspectEventPublisher.class);

    private Set<EventHandler> eventHandlers = new HashSet<EventHandler>();
    private Map<LocalDateTime, Object> eventObjects = new HashMap<>();

    public void registerEventHandler(EventHandler handler) {
        eventHandlers.add(handler);
    }

    @Override
    public void publish(Serializable event) {
        save(event);
    }

    private void save(Serializable event) {
        eventObjects.put(LocalDateTime.now(), event);
    }

    @Pointcut("@within(pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService) && " +
            "execution(* *.*(..))")
    public void applicationServiceMethods() {}

    @AfterReturning("applicationServiceMethods()")
    public void afterApplicationServiceMethod(JoinPoint joinPoint) {
        Iterator<Map.Entry<LocalDateTime, Object>> iterator = eventObjects.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .iterator();

        while (iterator.hasNext()) {
            Object event = eventObjects.remove(iterator.next().getKey());
            doPublish(event);
        }
    }

    protected void doPublish(Object event) {
        for (EventHandler handler : new ArrayList<EventHandler>(eventHandlers)) {
            if (handler.canHandle(event)) {
                try {
                    handler.handle(event);
                } catch (Exception e) {
                    LOGGER.error("event handling error", e);
                }
            }
        }
    }

}