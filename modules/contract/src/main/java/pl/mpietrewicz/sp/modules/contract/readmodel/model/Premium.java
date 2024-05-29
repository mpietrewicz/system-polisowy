package pl.mpietrewicz.sp.modules.contract.readmodel.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
public class Premium {

    private LocalDate validFrom;
    private BigDecimal amount;

}