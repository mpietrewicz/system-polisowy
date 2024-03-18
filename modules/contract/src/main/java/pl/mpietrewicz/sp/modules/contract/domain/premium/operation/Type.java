package pl.mpietrewicz.sp.modules.contract.domain.premium.operation;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

@ValueObject
public enum Type {

    ADD, CHANGE, DELETE;

}