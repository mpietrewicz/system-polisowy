package pl.mpietrewicz.sp.modules.contract.domain.premium.policy;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainPolicyImpl;
import pl.mpietrewicz.sp.modules.contract.domain.premium.operation.Operation;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@DomainPolicyImpl
public class OnceChangePremiumPolicy implements ChangePremiumPolicy {

    @Override
    public boolean isAvailable(List<Operation> operations, LocalDate date) {
        return operations.stream()
                .map(Operation::getDate)
                .filter(operationDate -> operationDate.isBefore(date))
                .max(LocalDate::compareTo)
                .filter(lastOperationDate -> YearMonth.from(lastOperationDate).plusYears(1).isBefore(YearMonth.from(date)))
                .isPresent();
    }

}