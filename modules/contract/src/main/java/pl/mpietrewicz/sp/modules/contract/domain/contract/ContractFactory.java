
package pl.mpietrewicz.sp.modules.contract.domain.contract;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainFactory;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.modules.contract.domain.contract.policy.ContractStartPolicy;
import pl.mpietrewicz.sp.modules.contract.domain.contract.policy.impl.MonthlyStartPolicy;

import java.time.LocalDate;

@DomainFactory
public class ContractFactory {

	public Contract createContract(String department, String name, LocalDate start, Frequency frequency) {
		AggregateId aggregateId = AggregateId.generate();
		ContractStartPolicy contractStartPolicy = new MonthlyStartPolicy();
		LocalDate contractStart = contractStartPolicy.getStartDate(start);
		String nameWithDepartment = department + "/" + name;

		return new Contract(aggregateId, nameWithDepartment, contractStart, frequency);
	}

}