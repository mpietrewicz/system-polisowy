package pl.mpietrewicz.sp.modules.accounting.application.api.impl;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.modules.accounting.application.api.SubledgerService;
import pl.mpietrewicz.sp.modules.accounting.domain.subledger.SubledgerRepository;

@ApplicationService
@RequiredArgsConstructor
public class SubledgerServiceImpl implements SubledgerService {

    private final SubledgerRepository subledgerRepository;

    @Override
    public void generateInterface() {
    }

}