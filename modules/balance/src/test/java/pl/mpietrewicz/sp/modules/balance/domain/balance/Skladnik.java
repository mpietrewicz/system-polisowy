package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "nrSkladnika")
public class Skladnik {

    private final String nrSkladnika;

}