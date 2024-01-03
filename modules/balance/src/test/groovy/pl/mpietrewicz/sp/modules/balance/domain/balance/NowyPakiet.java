package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NowyPakiet {

    private String idUmowy;
    private List<NowySkladnik> noweSkladniki;

}