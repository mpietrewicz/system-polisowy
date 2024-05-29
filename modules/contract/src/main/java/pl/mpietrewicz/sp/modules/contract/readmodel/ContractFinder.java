package pl.mpietrewicz.sp.modules.contract.readmodel;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.Finder;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ContractRepository;
import pl.mpietrewicz.sp.modules.contract.readmodel.converter.ContractConverter;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.Contract;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Finder
@RequiredArgsConstructor
public class ContractFinder {

    private final ContractRepository contractRepository;

    private final ContractConverter contractConverter;

    public List<Contract> find() {
        return contractRepository.find().stream()
                .map(contractConverter::convert)
                .collect(Collectors.toList());
    }

    public Optional<Contract> find(AggregateId contractId) {
        return contractRepository.findBy(contractId)
                .map(contractConverter::convert);
    }

}