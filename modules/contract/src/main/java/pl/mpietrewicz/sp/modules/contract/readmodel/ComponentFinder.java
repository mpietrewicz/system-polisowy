package pl.mpietrewicz.sp.modules.contract.readmodel;

import lombok.RequiredArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.application.Finder;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.contract.infrastructure.repo.ComponentRepository;
import pl.mpietrewicz.sp.modules.contract.readmodel.converter.ComponentConverter;
import pl.mpietrewicz.sp.modules.contract.readmodel.model.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Finder
@RequiredArgsConstructor
public class ComponentFinder {

    private final ComponentRepository componentRepository;

    private final ComponentConverter componentConverter;

    public List<Component> find(AggregateId contractId) {
        return componentRepository.findBy(contractId).stream()
                .map(componentConverter::convert)
                .collect(Collectors.toList());
    }

    public Optional<Component> find(AggregateId contractId, AggregateId componentId) {
        return componentRepository.findBy(contractId, componentId)
                .map(componentConverter::convert);
    }

}