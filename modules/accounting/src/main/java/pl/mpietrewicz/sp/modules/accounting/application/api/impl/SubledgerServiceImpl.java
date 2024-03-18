package pl.mpietrewicz.sp.modules.accounting.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.modules.accounting.application.api.SubledgerService;
import pl.mpietrewicz.sp.modules.accounting.infrastructure.repo.SubledgerRepository;

@ApplicationService(transactional = @Transactional(
        transactionManager = "accountingTransactionManager"))
@RequiredArgsConstructor
public class SubledgerServiceImpl implements SubledgerService {

    private final SubledgerRepository subledgerRepository;

    @Override
    public void generateInterface() {}

}