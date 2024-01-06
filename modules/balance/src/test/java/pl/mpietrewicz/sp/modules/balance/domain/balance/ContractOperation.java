package pl.mpietrewicz.sp.modules.balance.domain.balance;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Date;

@Getter
public class ContractOperation {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String ID_UMOWY;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String NR_SKLADNIKA;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String RODZAJ_SKL;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String ID_OPERACJI;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String OPERACJA;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date DATA_REJESTRACJI;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date DATA_ZMIANY;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String KTOWA;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date WPLACONO_DO;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String NADWYZKA;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ID_WZNOWIENIA;

}