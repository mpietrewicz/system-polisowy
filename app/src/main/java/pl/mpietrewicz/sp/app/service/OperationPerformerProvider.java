package pl.mpietrewicz.sp.app.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.mpietrewicz.sp.app.readmodel.model.Operation;

@Service
public class OperationPerformerProvider {

    private final OperationPerformer basicComponentOperationPerformer;

    private final OperationPerformer additionalComponentOperationPerformer;

    public OperationPerformerProvider(@Qualifier("basicComponentOperationPerformer")
                                              OperationPerformer basicComponentOperationPerformer,
                                      @Qualifier("additionalComponentOperationPerformer")
                                              OperationPerformer additionalComponentOperationPerformer) {
        this.basicComponentOperationPerformer = basicComponentOperationPerformer;
        this.additionalComponentOperationPerformer = additionalComponentOperationPerformer;
    }

    public OperationPerformer get(Operation operation) {
        if (isBasicComponent(operation.getComponent())) {
            return basicComponentOperationPerformer;
        } else {
            return additionalComponentOperationPerformer;
        }
    }

    private boolean isBasicComponent(String componentName) {
        return componentName.startsWith("P") || componentName.startsWith("D");
    }

}