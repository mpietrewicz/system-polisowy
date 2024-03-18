package pl.mpietrewicz.sp.modules.contract.readmodel.model;

import lombok.Builder;
import lombok.Getter;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentType;

import java.time.LocalDate;

@Builder
@Getter
public class Component {

    private String componentId;
    private String name;
    private LocalDate start;
    private ComponentType componentType;
    private LocalDate end;

}