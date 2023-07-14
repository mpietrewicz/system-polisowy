package pl.mpietrewicz.sp.modules.contract.readmodel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.mpietrewicz.sp.modules.contract.domain.component.ComponentType;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.ComponentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
public class SkladnikDto {

    private String idSkladnika;
    private String idUmowy;
    private LocalDate dataDokupienia;
    private BigDecimal skladka;
    private BigDecimal oczekiwaneSkladki;
    private ComponentStatus status;
    private ComponentType rodzaj;
    private LocalDate dataZakonczenia;

    public SkladnikDto(String idUmowy, LocalDate dataDokupienia, BigDecimal skladka) {
        this.idUmowy = idUmowy;
        this.dataDokupienia = dataDokupienia;
        this.skladka = skladka;
    }

    public SkladnikDto(String idSkladnika, String idUmowy, LocalDate dataDokupienia, ComponentStatus status,
                       ComponentType rodzaj) {
        this.idSkladnika = idSkladnika;
        this.idUmowy = idUmowy;
        this.dataDokupienia = dataDokupienia;
        this.status = status;
        this.rodzaj = rodzaj;
    }

    public SkladnikDto(String idSkladnika, LocalDate dataZakonczenia) {
        this.idSkladnika = idSkladnika;
        this.dataZakonczenia = dataZakonczenia;
    }
}