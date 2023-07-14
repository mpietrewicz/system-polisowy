package pl.mpietrewicz.sp.modules.finance.readmodel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class WplataDto {

    private String id;
    private LocalDate dataWplaty;
    private LocalDateTime dataRejestracji;
    private BigDecimal kwota;
    private String idUmowy;

    public String getId() {
        return id;
    }

    public LocalDate getDataWplaty() {
        return dataWplaty;
    }

    public LocalDateTime getDataRejestracji() {
        return dataRejestracji;
    }

    public BigDecimal getKwota() {
        return kwota;
    }

    public String getIdUmowy() {
        return idUmowy;
    }
}