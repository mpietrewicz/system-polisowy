package pl.mpietrewicz.sp.modules.contract.readmodel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZmianaSkladkiDto {

    private String idSkladnika;
    private BigDecimal skladka;
    private String dataZmiany;

}