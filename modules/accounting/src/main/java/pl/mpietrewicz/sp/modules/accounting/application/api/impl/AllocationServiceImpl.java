package pl.mpietrewicz.sp.modules.accounting.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.modules.accounting.application.api.AllocationService;
import pl.mpietrewicz.sp.modules.accounting.domain.allocation.Allocation;
import pl.mpietrewicz.sp.modules.accounting.domain.allocation.AllocationFactory;
import pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.AllocationRepository;

import javax.persistence.NoResultException;
import java.util.List;

@ApplicationService(transactional = @Transactional(
        transactionManager = "accountingTransactionManager"))
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    private final AllocationRepository allocationRepository;
    private final AllocationFactory allocationFactory;

    @Override
    public void update(AggregateId contractId, List<MonthlyBalance> monthlyBalances) {
        List<RiskDefinition> riskDefinitions = allocationRepository.findRiskDefinitions(contractId);
        Allocation allocation = getAllocation(contractId, monthlyBalances, riskDefinitions);

        allocation.update(monthlyBalances, riskDefinitions);
    }

    private Allocation getAllocation(AggregateId contractId, List<MonthlyBalance> monthlyBalances,
                                     List<RiskDefinition> riskDefinitions) {
        try {
            return allocationRepository.findByContractId(contractId);
        } catch (NoResultException e) {
            return allocationFactory.create(contractId, monthlyBalances, riskDefinitions);
        }
    }

}