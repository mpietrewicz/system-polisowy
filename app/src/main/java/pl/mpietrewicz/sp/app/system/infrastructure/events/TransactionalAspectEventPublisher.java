
package pl.mpietrewicz.sp.app.system.infrastructure.events;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.app.system.infrastructure.events.impl.PendingPublication;
import pl.mpietrewicz.sp.app.system.infrastructure.events.impl.handlers.EventHandler;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.annotations.event.Event;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Aspect
@Primary
@Component
public class TransactionalAspectEventPublisher implements DomainEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalAspectEventPublisher.class);

    private Set<EventHandler> eventHandlers = new HashSet<EventHandler>();
    private final List<PendingPublication> pendingPublications = new ArrayList<>();
    private final Lock lock = new ReentrantLock();

    public void registerEventHandler(EventHandler handler) {
        eventHandlers.add(handler);
    }

    @Override
    public void publish(Serializable event) {
        if (event.getClass().isAnnotationPresent(Event.class)) {
            Event annotation = event.getClass().getAnnotation(Event.class);
            String boundedContext = annotation.boundedContext();

            addPendingPublication(event, boundedContext);
        }
    }

    @Pointcut("@within(pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService) && " +
            "execution(* *.*(..))")
    public void applicationServiceMethods() {
    }

    @AfterReturning("applicationServiceMethods()")
    public void afterApplicationServiceMethod(JoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (targetClass.isAnnotationPresent(ApplicationService.class)) {
            ApplicationService annotation = targetClass.getAnnotation(ApplicationService.class);
            String boundedContext = annotation.boundedContext();

            for (PendingPublication publication : getPendingPublication(boundedContext)) {
                doPublish(publication.getEvent());
            }
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

    public void addPendingPublication(Serializable event, String boundedContext) {
        lock.lock();
        try {
            pendingPublications.add(new PendingPublication(event, boundedContext));
        } finally {
            lock.unlock();
        }
    }

    public List<PendingPublication> getPendingPublication(String boundedContext) {
        lock.lock();
        try {
            List<PendingPublication> events = pendingPublications.stream()
                    .filter(event -> event.getBoundedContext().equals(boundedContext))
                    .sorted(Comparator.comparing(PendingPublication::getCreated))
                    .collect(Collectors.toList());
            pendingPublications.removeAll(events);
            return events;
        } finally {
            lock.unlock();
        }
    }

}