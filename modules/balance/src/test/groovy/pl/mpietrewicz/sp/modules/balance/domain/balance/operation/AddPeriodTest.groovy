//file:noinspection NonAsciiCharacters
package pl.mpietrewicz.sp.modules.balance.domain.balance.operation

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.Month
import spock.lang.Specification
import spock.lang.Subject

import java.time.YearMonth

class AddPeriodTest extends Specification {

    @Subject
    AddPeriod addPeriod = new AddPeriod(YearMonth.parse("2023-01"), Frequency.MONTHLY)

    def "Powinien dodać nowy miesiąc"() {
        def previousMonth = new Month(
                YearMonth.parse("2022-12"),
                new BigDecimal("10"),
                MonthStatus.UNPAID,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        )
        def previousPeriodCopy = new Period(Arrays.asList(previousMonth))
        when:
        addPeriod.execute(previousPeriodCopy)
        then:
        addPeriod.getLastAfectedMonth() == YearMonth.parse("2023-01")
    }

    def "Powinien zwrócić błąd, gdy miedzy ostantim miesiącem, a otwieranym jest przerwa"() {
        def previousMonth = new Month(
                YearMonth.parse("2022-11"),
                new BigDecimal("10"),
                MonthStatus.UNPAID,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        )
        def previousPeriodCopy = new Period(Arrays.asList(previousMonth))
        when:
        addPeriod.execute(previousPeriodCopy)
        then:
        thrown(IllegalStateException)
    }

    def "Powinien zwrócić błąd, gdy otwierany miesiąc nie jest ostatnim miesiącem poprzedniego okresu"() {
        def previousMonth = new Month(
                YearMonth.parse("2022-02"),
                new BigDecimal("10"),
                MonthStatus.UNPAID,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        )
        def previousPeriodCopy = new Period(Arrays.asList(previousMonth))
        when:
        addPeriod.execute(previousPeriodCopy)
        then:
        thrown(IllegalStateException)
    }

}