package pl.mpietrewicz.sp.modules.finance.readmodel;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.Finder;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.finance.infrastructure.repo.PaymentRepository;
import pl.mpietrewicz.sp.modules.finance.infrastructure.repo.RefundRepository;
import pl.mpietrewicz.sp.modules.finance.readmodel.converter.PaymentConverter;
import pl.mpietrewicz.sp.modules.finance.readmodel.model.Payment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Finder
@RequiredArgsConstructor
public class FinanceFinder {

    private final PaymentRepository paymentRepository;

    private final PaymentConverter paymentConverter;

    private final RefundRepository refundRepository;

    public List<Payment> find(AggregateId contractId) {
        return Stream.concat(
                paymentRepository.findBy(contractId).stream()
                        .map(paymentConverter::convert),
                refundRepository.findBy(contractId).stream()
                        .map(paymentConverter::convert)
        ).collect(Collectors.toList());
    }

    public Payment find(AggregateId contractId, AggregateId paymentId) {
        return paymentRepository.findBy(contractId, paymentId).stream()
                .findAny()
                .map(paymentConverter::convert)
                .orElseThrow();
    }


}