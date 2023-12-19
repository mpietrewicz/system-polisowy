package pl.mpietrewicz.sp.modules.finance.application.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.mpietrewicz.sp.ddd.annotations.application.ApplicationService;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.modules.contract.application.api.ContractService;
import pl.mpietrewicz.sp.modules.finance.application.api.PaymentService;
import pl.mpietrewicz.sp.modules.finance.domain.payment.PaymentDomainService;
import pl.mpietrewicz.sp.modules.finance.domain.payment.RegisterPayment;
import pl.mpietrewicz.sp.modules.finance.infrastructure.repo.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@ApplicationService(transactional = @Transactional(
        transactionManager = "financeTransactionManager"))
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;
    private final ContractService contractService;

    @Override
    public void addPayment(String contractId, BigDecimal amount, LocalDate date) {
        ContractData contractData = contractService.getContractData(new AggregateId(contractId));
        RegisterPayment registerPayment = paymentDomainService.createPayment(contractData, amount, date);
        paymentRepository.save(registerPayment);
    }

}