package pl.mpietrewicz.sp.modules.accounting.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
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
    public void update(ContractData contractData, List<MonthlyBalance> monthlyBalances) {
        List<RiskDefinition> riskDefinitions = allocationRepository.findRiskDefinitions(contractData.getAggregateId());
        Allocation allocation = getAllocation(contractData, monthlyBalances, riskDefinitions);

        allocation.update(monthlyBalances, riskDefinitions);
    }

    private Allocation getAllocation(ContractData contractData, List<MonthlyBalance> monthlyBalances,
                                     List<RiskDefinition> riskDefinitions) {
        try {
            return allocationRepository.findByContractId(contractData.getAggregateId());
        } catch (NoResultException e) {
            return allocationFactory.create(contractData, monthlyBalances, riskDefinitions);
        }
    }

}