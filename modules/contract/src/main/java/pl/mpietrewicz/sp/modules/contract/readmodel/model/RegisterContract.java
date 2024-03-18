package pl.mpietrewicz.sp.modules.contract.readmodel.model;

import lombok.Builder;
import lombok.Getter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
public class RegisterContract {

    private LocalDate start;
    private String name;
    private BigDecimal premium;
    private Frequency frequency;

}