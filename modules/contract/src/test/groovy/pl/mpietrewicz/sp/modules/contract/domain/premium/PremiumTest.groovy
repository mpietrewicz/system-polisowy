package pl.mpietrewicz.sp.modules.contract.domain.premium

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ComponentData
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class PremiumTest extends Specification {

    def "should add new premium started 2023-03-01"() {
        given:
        def contractData = createContractData("contract")
        def componentData = createComponentData("component_1")
        def premium = preparePremium(contractData)

        when:
        premium.add(componentData, LocalDate.parse("2023-03-03"), new Amount("5"))

        then:
        premium.generateSnapshot(LocalDateTime.now())
                .getAmountAt(LocalDate.parse("2023-04-01"))
                .equals(new Amount("5"));
        premium.generateSnapshot(LocalDateTime.now())
                .getAmountAt(LocalDate.parse("2023-03-01"))
                .equals(new Amount("5"));
        premium.generateSnapshot(LocalDateTime.now())
                .getAmountAt(LocalDate.parse("2023-02-01"))
                .equals(new Amount("0"));
    }

    def "should change premium started 2023-04-01"() {
        given:
        def contractData = createContractData("contract")
        def componentData = createComponentData("component_1")

        def premium = preparePremium(contractData)
        premium.add(componentData, LocalDate.parse("2023-01-01"), Amount.TEN)

        when:
        premium.change(componentData, LocalDate.parse("2023-04-15"), new Amount("15"))

        then:
        premium.generateSnapshot(LocalDateTime.now())
                .getAmountAt(LocalDate.parse("2023-04-01"))
                .equals(new Amount("15"));
        premium.generateSnapshot(LocalDateTime.now())
                .getAmountAt(LocalDate.parse("2023-03-01"))
                .equals(new Amount("10"));
    }

    def "should delete premium after 2023-10-31"() {
        given:
        def contractData = createContractData("contract")
        def componentData = createComponentData("component_1")

        def premium = preparePremium(contractData)
        premium.add(componentData, LocalDate.parse("2023-01-01"), Amount.TEN)

        when:
        premium.delete(componentData, LocalDate.parse("2023-10-31"))

        then:
        premium.generateSnapshot(LocalDateTime.now())
                .getAmountAt(LocalDate.parse("2023-10-31"))
                .equals(new Amount("10"));
        premium.generateSnapshot(LocalDateTime.now())
                .getAmountAt(LocalDate.parse("2023-11-01"))
                .equals(new Amount("0"));
    }

    private static ContractData createContractData(id) {
        new ContractData(new AggregateId(id))
    }

    private static ComponentData createComponentData(id) {
        new ComponentData(new AggregateId(id))
    }

    private static ComponentPremium createComponentPremium(ComponentData componentData) {
        new ComponentPremium(componentData)
    }


    private static Premium preparePremium(contractData) {
        def premium = new Premium(AggregateId.generate(), contractData)
//        premium.eventPublisher = Mock(DomainEventPublisher)
    }
}