package pl.mpietrewicz.sp.modules.contract.readmodel.converter;

import org.springframework.stereotype.Service;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.Component;

@Service
public class ComponentConverter {

    public Component convert(pl.mpietrewicz.sp.modules.contract.domain.component.Component component) {
        return Component.builder()
                .componentId(component.getAggregateId().getId())
                .name(component.getName())
                .start(component.getStart())
                .componentType(component.getComponentType())
                .end(component.getEnd())
                .build();
    }

}