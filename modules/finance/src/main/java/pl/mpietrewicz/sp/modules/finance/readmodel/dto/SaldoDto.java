package pl.mpietrewicz.sp.modules.finance.readmodel.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class SaldoDto {

    private BigDecimal sumaSkladek;
    private BigDecimal sumaWplat;
    private BigDecimal saldo;
    private LocalDate wplaconoDo;
    private BigDecimal nadwyzka;

    public SaldoDto() {
    }

    public SaldoDto(BigDecimal sumaSkladek, BigDecimal sumaWplat, BigDecimal saldo, LocalDate wplaconoDo, BigDecimal nadwyzka) {
        this.sumaSkladek = sumaSkladek;
        this.sumaWplat = sumaWplat;
        this.saldo = saldo;
        this.wplaconoDo = wplaconoDo;
        this.nadwyzka = nadwyzka;
    }
}