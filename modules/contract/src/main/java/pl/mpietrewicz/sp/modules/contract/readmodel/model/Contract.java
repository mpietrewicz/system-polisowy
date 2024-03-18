package pl.mpietrewicz.sp.modules.contract.readmodel.model;

import lombok.Builder;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;

import java.time.LocalDate;

@Builder
@Getter
public class Contract {

    private String contractId;
    private LocalDate start;
    private Frequency frequency;
    private LocalDate end;

}