package pl.mpietrewicz.sp.modules.accounting.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.modules.accounting.application.api.AccountingService;
import pl.mpietrewicz.sp.modules.accounting.domain.accounting.Accounting;
import pl.mpietrewicz.sp.modules.accounting.domain.accounting.AccountingRepository;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;

@ApplicationService
@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {

    private final AccountingRepository accountingRepository;

    @Override
    public void openNewMonth() {
        Accounting accounting = accountingRepository.find();
        accounting.shiftMonth();
        // todo: wysłać zdarzenie
    }

}