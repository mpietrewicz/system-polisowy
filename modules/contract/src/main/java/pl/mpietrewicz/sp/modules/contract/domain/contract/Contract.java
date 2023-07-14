
package pl.mpietrewicz.sp.modules.contract.domain.contract;

import pl.mpietrewicz.sp.SystemParameters;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.support.domain.BaseAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@AggregateRoot
public class Contract extends BaseAggregateRoot {

	private LocalDate startDate;
	private YearMonth accountingMonth = SystemParameters.CURRENT_ACCOUNTING_MONTH;

	@Enumerated(EnumType.STRING)
	private ContractStatus contractStatus;

	@Enumerated(EnumType.STRING)
	private Frequency frequency;

	public Contract() {
	}

	public Contract(AggregateId aggregateId, LocalDate startDate, Frequency frequency) {
		this.aggregateId = aggregateId;
		this.startDate = startDate;
		this.contractStatus = initStatus(startDate);
		this.frequency = frequency;
	}

	public ContractData generateSnapshot() {
		return new ContractData(aggregateId, startDate, frequency);
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	public YearMonth getAccountingMonth() {
		return accountingMonth;
	}

	public void shiftAccountingMonth(YearMonth month) {
		if (getAccountingMonth().compareTo(month) < 0) {
			shiftAccountingMonth();
		}
	}

	public void shiftAccountingMonth() {
		if (isContractOpen()) {
			accountingMonth = accountingMonth.plusMonths(1);
			// todo wyślij zdarzenie
		}
	}

	private boolean isContractOpen() {
		return contractStatus == ContractStatus.OPEN;
	}

	// todo: gdy zostanie przwinienty miesiac nalezy tez zaktualizowac status!
	private ContractStatus initStatus(LocalDate startDate) {
		if (YearMonth.from(startDate).compareTo(SystemParameters.getCurrentAccountingMonth()) > 0) {
			return ContractStatus.WAITING;
		} else {
			return ContractStatus.OPEN;
		}
	}
}