package acceptancetests.dataread;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "idPakietu")
public class Pakiet {

    private final String idPakietu;

}