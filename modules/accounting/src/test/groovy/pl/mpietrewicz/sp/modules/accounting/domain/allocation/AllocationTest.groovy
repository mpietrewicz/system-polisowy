//file:noinspection NonAsciiCharacters
package pl.mpietrewicz.sp.modules.accounting.domain.allocation


import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.MonthlyBalance
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.RiskDefinition
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.PositiveAmount
import pl.mpietrewicz.sp.ddd.sharedkernel.valueobject.Divisor
import spock.lang.Specification
import spock.lang.Subject

import java.time.YearMonth

class AllocationTest extends Specification { // todo: do dodrawy będą te testy

    @Subject
    Allocation allocation = new Allocation(new ContractData())

    def "Nie powinien aktualizować przypisu gdy brak danych"() {
        given:
        List<MonthlyBalance> monthlyBalances = new ArrayList<>()
        List<RiskDefinition> riskDefinition = new ArrayList<>()
        when:
        allocation.update(monthlyBalances, riskDefinition)
        then:
        allocation.getAmount().equals(PositiveAmount.TEN)
    }

    def "Powinien utworzyć nowy przypis"() {
        given:
        List<MonthlyBalance> monthlyBalances = List.of(
                MonthlyBalance.builder()
                        .month(YearMonth.parse("2023-03"))
                        .build())
        List<RiskDefinition> riskDefinition = List.of(
                RiskDefinition.builder()
                        .id(1L)
                        .premiumDivisor(new Divisor(80))
                        .build(),
                RiskDefinition.builder()
                        .id(2L)
                        .premiumDivisor(new Divisor(20))
                        .build())
        when:
        allocation.update(monthlyBalances, riskDefinition)
        then:
        allocation.getAmount().subtract(PositiveAmount.TEN) == 0
    }

    def "Powinien skorygować istniejący przypis nowy przypis"() {
        given:
        List<MonthlyBalance> monthlyBalances = List.of(
                MonthlyBalance.builder()
                        .month(YearMonth.parse("2023-03"))
                        .build())
        List<RiskDefinition> riskDefinition = List.of(
                RiskDefinition.builder()
                        .id(1L)
                        .premiumDivisor(new Divisor(80))
                        .build(),
                RiskDefinition.builder()
                        .id(2L)
                        .premiumDivisor(new Divisor(20))
                        .build())
        when:
        allocation.update(monthlyBalances, riskDefinition)
        then:
        allocation.getAmount().subtract(PositiveAmount.TEN).equals(PositiveAmount.TEN)
    }

}