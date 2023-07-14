
package pl.mpietrewicz.sp.app.system.saga;

public interface SagaEngine {

    void handleSagasEvent(Object event);
}