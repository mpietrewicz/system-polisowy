package pl.mpietrewicz.sp.modules.finance.readmodel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrzypisDto {

    private Long id;
    private LocalDate okresOd;
    private LocalDate okresDo;
    private BigDecimal skladka;

}