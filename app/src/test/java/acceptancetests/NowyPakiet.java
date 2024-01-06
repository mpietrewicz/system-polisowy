package acceptancetests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NowyPakiet {

    private String idUmowy;
    private List<NowySkladnik> noweSkladniki;

}