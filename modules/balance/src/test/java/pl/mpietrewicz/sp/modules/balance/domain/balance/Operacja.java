package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class Operacja {

    Skladnik skladnik;
    String rodzaj;
    Date data;
    String kwota;

}