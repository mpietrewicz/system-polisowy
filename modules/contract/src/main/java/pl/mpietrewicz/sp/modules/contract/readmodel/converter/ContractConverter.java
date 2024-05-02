package pl.mpietrewicz.sp.modules.contract.readmodel.converter;

import org.springframework.stereotype.Service;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.Contract;

@Service
public class ContractConverter {

    public Contract convert(pl.mpietrewicz.sp.modules.contract.domain.contract.Contract contract) {
        return Contract.builder()
                .contractId(contract.getAggregateId().getId())
                .name(contract.getName())
                .start(contract.getStart())
                .frequency(contract.getFrequency())
                .end(contract.getEnd())
                .build();
    }

}