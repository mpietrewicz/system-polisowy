package pl.mpietrewicz.sp.modules.balance.domain.balance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonReader {

    public List<NowyPakiet> read() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File file = new File("src/test/java/pl/mpietrewicz/sp/modules/balance/domain/balance/data.json");

            // Zdeserializuj kolekcję obiektów JSON
            List<ContractOperation> contractOperations = objectMapper.readValue(file, new TypeReference<List<ContractOperation>>() {});

            Map<Pakiet, Map<Skladnik, List<ContractOperation>>> map = new HashMap<>();

            for (ContractOperation operation : contractOperations) {

                map.computeIfAbsent(new Pakiet(operation.getID_UMOWY()), k -> new HashMap<>())
                        .computeIfAbsent(new Skladnik(operation.getNR_SKLADNIKA()), k -> new ArrayList<>())
                                .add(operation);
                // itd.
            }

            List<NowyPakiet> nowePakiety = new ArrayList<>();

            for (Map.Entry<Pakiet, Map<Skladnik, List<ContractOperation>>> entry : map.entrySet()) {
                Pakiet pakiet = entry.getKey();
                List<NowySkladnik> nowySkladniki = new ArrayList<>();

                for (Map.Entry<Skladnik, List<ContractOperation>> skladnikEntry : entry.getValue().entrySet()) {
                    Skladnik skladnik = skladnikEntry.getKey();
                    List<ContractOperation> contractOperations2 = skladnikEntry.getValue();
                    NowySkladnik nowySkladnik = new NowySkladnik(skladnik.getNrSkladnika(), contractOperations2);
                    nowySkladniki.add(nowySkladnik);
                }

                NowyPakiet nowyPakiet = new NowyPakiet(pakiet.getIdPakietu(), nowySkladniki);
                nowePakiety.add(nowyPakiet);
            }

            return nowePakiety;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}