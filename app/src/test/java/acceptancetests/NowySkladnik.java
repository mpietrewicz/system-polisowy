package acceptancetests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NowySkladnik {

    private String nrSkladnika;
    private List<ContractOperation> contractOperations;

}