package pl.mpietrewicz.sp.modules.contract.readmodel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.modules.contract.domain.contract.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolisaDto {

    private String id;
    private LocalDate dataRejestracji;
    private ContractStatus status;
    private BigDecimal skladka;
    private Frequency czestotliwosc;
    private PaymentPolicy typ;

    public PolisaDto(String id, LocalDate dataRejestracji) {
        this.id = id;
        this.dataRejestracji = dataRejestracji;
    }

    public PolisaDto(String id, LocalDate dataRejestracji, ContractStatus status) {
        this.id = id;
        this.dataRejestracji = dataRejestracji;
        this.status = status;
    }

    public PolisaDto(String id, BigDecimal skladka, Frequency czestotliwosc) {
        this.id = id;
        this.skladka = skladka;
        this.czestotliwosc = czestotliwosc;
    }
}