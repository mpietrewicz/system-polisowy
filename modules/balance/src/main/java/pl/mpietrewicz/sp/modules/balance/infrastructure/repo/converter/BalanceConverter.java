package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.converter;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Balance;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.OperationEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.BalanceEntity;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BalanceConverter {

    @Inject
    private OperationConverter operationConverter;

    @Inject
    private AutowireCapableBeanFactory spring;

    public Balance convert(BalanceEntity entity) {
        List<Operation> operations = entity.getOperations().stream()
                .map(e -> operationConverter.convert(e))
                .collect(Collectors.toList());
        Balance balance = new Balance(entity.getAggregateId(), entity.getVersion(), entity.getContractId(), operations);
        spring.autowireBean(balance);
        return balance;
    }

    public BalanceEntity convert(Balance model) {
        List<OperationEntity> operationEntities = model.getOperations().stream()
                .map(m -> operationConverter.convert(m))
                .collect(Collectors.toList());
        return new BalanceEntity(model.getAggregateId(), model.getVersion(), model.getContractId(), operationEntities);
    }

}