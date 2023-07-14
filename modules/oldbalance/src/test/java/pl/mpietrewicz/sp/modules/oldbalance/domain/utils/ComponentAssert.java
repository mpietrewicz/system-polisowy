package pl.mpietrewicz.sp.modules.oldbalance.domain.utils;

import pl.mpietrewicz.sp.modules.contract.domain.component.Component;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentAssert {

    private final Component component;

    public ComponentAssert(Component component) {
        this.component = component;
    }

    public void isClose() {
        assertThat(component.isContractOpen()).isFalse();
    }

}