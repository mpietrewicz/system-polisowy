package pl.mpietrewicz.sp.modules.balance.domain.balance;

import org.junit.Test;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.CONTINUATION;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.NO_RENEWAL;
import static pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum.RENEWAL;

public class TestBalance {

    @Test
    public void newTest() {
        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, Amount.TEN, contractData.getAggregateId());
        balance.addPayment(date("2023-02-10"), amount("30"), CONTINUATION);
        balance.addRefund(date("2023-03-10"), amount("20"));
        balance.addPayment(date("2023-05-20"), amount("60"), CONTINUATION);
        balance.changePremium(LocalDate.parse("2023-02-01"), new Amount("100"), contractData.getAggregateId());

        System.out.println("koniec");
    }

    @Test
    public void testProductionExample() {
        LocalDate contractStart = LocalDate.parse("2022-03-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, new Amount("11.78"), contractData.getAggregateId());
        balance.addPayment(date("2022-02-25"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2022-06-27"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2022-09-14"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2022-12-14"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2023-05-29"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2023-06-15"), amount("35.34"), CONTINUATION);
        balance.addPayment(date("2023-09-14"), amount("35.34"), CONTINUATION);

        System.out.println("koniec");
    }

    @Test
    public void testNonRenewalPolicy() {
        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, Amount.TEN, contractData.getAggregateId());
        balance.addPayment(date("2023-02-10"), amount("30"), NO_RENEWAL);
        balance.addRefund(date("2023-03-10"), amount("20"));
        balance.addPayment(date("2023-05-20"), amount("60"), NO_RENEWAL);

        System.out.println("koniec");
    }

    @Test
    public void testRenewalPolicy() { // todo: jest błąd, bo ostani okres jest UNDERPAID, a powinien być UNPAID
        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        balance.startCalculating(contractStart, Amount.TEN, contractData.getAggregateId());
        balance.addPayment(date("2023-02-10"), amount("30"), NO_RENEWAL);
        balance.addRefund(date("2023-03-10"), amount("20"));
        balance.addPayment(date("2023-10-20"), amount("60"), RENEWAL);

        System.out.println("koniec");
    }

    @Test
    public void productionTest() throws IOException {

        JsonReader jsonReader = new JsonReader();
        List<NowyPakiet> daneDoTestow = jsonReader.read();

        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);

        List<TestData> testData = List.of(
                new TestData("ZUM", "2001-02-06 00:00:00", "2001-01-01", "11.96"),
                new TestData("Wpl", "2001-02-12 00:00:00", "2001-02-07", "35.9"),
                new TestData("Wpl", "2001-05-04 00:00:00", "2001-04-27", "35.9"),
                new TestData("Wpl", "2001-08-02 08:02:00", "2001-08-01", "35.9"),
                new TestData("Wpl", "2001-10-24 12:38:00", "2001-10-24", "35.9"),
                new TestData("PSU", "2002-02-04 00:00:00", "2002-01-04", "13.16"),
                new TestData("Wpl", "2002-02-04 16:23:00", "2002-02-04", "39.5"),
                new TestData("Wpl", "2002-04-22 16:18:00", "2002-04-22", "39.5"),
                new TestData("Wpl", "2002-04-22 16:18:00", "2002-04-22", "39.5"),
                new TestData("Wpl", "2002-04-22 16:18:00", "2002-04-22", "39.5"),
                new TestData("Wpl", "2003-02-05 21:30:00", "2003-01-14", "39.5"),
                new TestData("Wpl", "2003-05-06 14:30:00", "2003-05-05", "39.5"),
                new TestData("Wpl", "2003-07-30 15:51:00", "2003-07-30", "39.5"),
                new TestData("Wpl", "2003-10-22 15:45:00", "2003-10-22", "39.5"),
                new TestData("PSU", "2004-01-27 00:00:00", "2004-01-27", "13.76"),
                new TestData("Wpl", "2004-01-27 17:05:00", "2004-01-27", "41.3"),
                new TestData("Wpl", "2004-04-30 08:30:00", "2004-04-29", "41.3")
        );

        testData.stream()
                .map(TestDataConverter::convert)
                .forEach(operationToTestData -> runBalanceMethod(balance, contractData, operationToTestData));

        System.out.println("koniec");
    }

    private void runBalanceMethod(Balance balance, ContractData contractData, OperationToTestData operationToTestData) {
        if (operationToTestData.getOperationEnum() == OperationEnum.START_CONTRACT) {
            balance.startCalculating(operationToTestData.getDate(), new Amount(operationToTestData.getAmount()), contractData.getAggregateId());
        } else if (operationToTestData.getOperationEnum() == OperationEnum.PAYMENT) {
            balance.addPayment(operationToTestData.getDate(), new Amount(operationToTestData.getAmount()), RENEWAL);
        } else if (operationToTestData.getOperationEnum() == OperationEnum.INCREASE_INSURANCE_SUM) {
            balance.changePremium(operationToTestData.getDate(), new Amount(operationToTestData.getAmount()), contractData.getAggregateId());
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Test
    public void productionTestNew() throws IOException {

        JsonReader jsonReader = new JsonReader();
        List<NowyPakiet> daneDoTestow = jsonReader.read();

        LocalDate contractStart = LocalDate.parse("2023-01-01");
        ContractData contractData = contractDate(contractStart);

        Balance balance = new Balance(AggregateId.generate(), contractData);


        List<ContractOperation> sortedOperations = daneDoTestow.get(3)
                .getNoweSkladniki().stream()
                .map(NowySkladnik::getContractOperations)
                .flatMap(Collection::stream)
                .filter(c -> c.getDATA_ZMIANY() != null)
                .sorted(Comparator.comparing(ContractOperation::getDATA_REJESTRACJI)
                        .thenComparing(ContractOperation::getDATA_ZMIANY))
                .collect(Collectors.toList());

        for (ContractOperation operation : sortedOperations) {
            newRunBalanceMethod(balance, operation);
        }

        System.out.println("koniec");
    }

    private void newRunBalanceMethod(Balance balance, ContractOperation contractOperation) {
        if (contractOperation.getOPERACJA().equals("ZUM")) {
            balance.startCalculating(
                    convertToLocalDate(contractOperation.getDATA_ZMIANY()),
                    new Amount(contractOperation.getKTOWA().replace(",", ".")),
                    new AggregateId(contractOperation.getNR_SKLADNIKA())
            );
        } else if (contractOperation.getOPERACJA().equals("Wplata")) {
            balance.addPayment(
                    convertToLocalDate(contractOperation.getDATA_ZMIANY()),
                    new Amount(contractOperation.getKTOWA().replace(",", ".")),
                    RENEWAL
            );
        } else if (List.of("PSU", "DSK").contains(contractOperation.getOPERACJA())) {
            balance.changePremium(
                    convertToLocalDate(contractOperation.getDATA_ZMIANY()),
                    new Amount(contractOperation.getKTOWA().replace(",", ".")),
                    new AggregateId(contractOperation.getNR_SKLADNIKA())
            );
        } else {
            throw new IllegalArgumentException();
        }
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    private ContractData contractDate(LocalDate start) {
        return new ContractData(AggregateId.generate(), start, null, CONTINUATION, YearMonth.from(start));
    }

    private LocalDate date(String val) {
        return LocalDate.parse(val);
    }

    private Amount amount(String val) {
        return new Amount(val);
    }

}