
package pl.mpietrewicz.sp.app.system.infrastructure.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.ddd.support.domain.DomainEventPublisher;
import pl.mpietrewicz.sp.app.system.infrastructure.events.impl.handlers.EventHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component
public class SimpleEventPublisher implements DomainEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventPublisher.class);

    private Set<EventHandler> eventHandlers = new HashSet<EventHandler>();

    public void registerEventHandler(EventHandler handler) {
        eventHandlers.add(handler);
        // new SpringEventHandler(eventType, beanName, method));
    }

    @Override
    public void publish(Serializable event, String serviceName) {
        doPublish(event);
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