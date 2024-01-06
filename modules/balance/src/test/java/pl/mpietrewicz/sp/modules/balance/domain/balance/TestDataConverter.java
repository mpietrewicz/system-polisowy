package pl.mpietrewicz.sp.modules.balance.domain.balance;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestDataConverter {

    public static OperationToTestData convert(TestData testData) {
        return new OperationToTestData(
                OperationEnum.get(testData.getOperation()),
                LocalDate.parse(testData.getChange()),
                new BigDecimal(testData.getAmount())
        );
    }


}