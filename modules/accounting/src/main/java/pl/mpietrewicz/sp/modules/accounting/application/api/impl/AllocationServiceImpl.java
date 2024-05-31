package pl.mpietrewicz.sp.modules.accounting.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.modules.accounting.application.api.AllocationService;
import pl.mpietrewicz.sp.modules.accounting.domain.allocation.Allocation;
import pl.mpietrewicz.sp.modules.accounting.domain.allocation.AllocationFactory;
import pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.AllocationRepository;

import java.util.List;
import java.util.Optional;

@ApplicationService(boundedContext = "accounting", transactionManager = "accountingTransactionManager")
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    private final AllocationRepository allocationRepository;

    private final AllocationFactory allocationFactory;

    @Override
    public void update(AggregateId contractId, List<MonthlyBalance> monthlyBalances) {
        List<RiskDefinition> riskDefinitions = allocationRepository.findRiskDefinitions(contractId);
        Optional<Allocation> allocation = allocationRepository.findByContractId(contractId);

        if (allocation.isPresent()) {
            allocation.get().add(monthlyBalances, riskDefinitions);
        } else {
            Allocation newAllocation = allocationFactory.create(contractId);
            newAllocation.add(monthlyBalances, riskDefinitions);
            allocationRepository.save(newAllocation);
        }
    }

}