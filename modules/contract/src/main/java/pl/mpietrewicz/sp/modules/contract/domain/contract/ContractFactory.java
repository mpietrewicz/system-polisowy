
package pl.mpietrewicz.sp.modules.contract.domain.contract;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicyEnum;
import pl.mpietrewicz.sp.modules.contract.domain.contract.policy.ContractStartDatePolicy;
import pl.mpietrewicz.sp.modules.contract.domain.contract.policy.impl.FirstDayOfMonthCSDPolicy;

import java.time.LocalDate;
import java.time.YearMonth;

@DomainFactory
public class ContractFactory {

	public Contract createContract(LocalDate registerDate, Frequency frequency, PaymentPolicyEnum paymentPolicyEnum) {
		AggregateId aggregateId = AggregateId.generate();
		ContractStartDatePolicy contractStartDatePolicy = new FirstDayOfMonthCSDPolicy();
		LocalDate contractStart = contractStartDatePolicy.specifyStartDate(registerDate);
		YearMonth accountingMonth = YearMonth.from(registerDate);

		return new Contract(aggregateId, contractStart, frequency, paymentPolicyEnum, accountingMonth);
	}
}