
package pl.mpietrewicz.sp.modules.contract.domain.contract;

import pl.mpietrewicz.sp.SystemParameters;
import pl.mpietrewicz.sp.ddd.annotations.domain.AggregateRoot;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.Frequency;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.snapshot.ContractData;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.PaymentPolicy;
import pl.mpietrewicz.sp.ddd.support.domain.BaseAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.YearMonth;

import static pl.mpietrewicz.sp.modules.contract.domain.contract.ContractStatus.PENDING;

@Entity
@AggregateRoot
public class Contract extends BaseAggregateRoot {

	private LocalDate startDate;
	private YearMonth accountingMonth = SystemParameters.CURRENT_ACCOUNTING_MONTH;

	@Enumerated(EnumType.STRING)
	private ContractStatus contractStatus = PENDING;

	@Enumerated(EnumType.STRING)
	private Frequency frequency;

	@Enumerated(EnumType.STRING)
	PaymentPolicy paymentPolicy;

	public Contract() {
	}

	public Contract(AggregateId aggregateId, LocalDate startDate, Frequency frequency, PaymentPolicy paymentPolicy) {
		this.aggregateId = aggregateId;
		this.startDate = startDate;
		this.frequency = frequency;
		this.paymentPolicy = paymentPolicy;
	}

	public ContractData generateSnapshot() {
		return new ContractData(aggregateId, startDate, frequency, paymentPolicy);
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
		return contractStatus == ContractStatus.ACTIVE;
	}

	// todo: gdy zostanie przwinienty miesiac nalezy tez zaktualizowac status! -> czyli balance odpowaida za aktualny status contract
	private ContractStatus initStatus(LocalDate startDate) {
		if (YearMonth.from(startDate).compareTo(SystemParameters.getCurrentAccountingMonth()) > 0) {
			return PENDING;
		} else {
			return ContractStatus.ACTIVE;
		}
	}
}