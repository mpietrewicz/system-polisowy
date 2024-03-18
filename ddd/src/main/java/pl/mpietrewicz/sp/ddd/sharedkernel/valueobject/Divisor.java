
package pl.mpietrewicz.sp.ddd.sharedkernel.valueobject;

import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 */
@SuppressWarnings("serial")
@Embeddable
@ValueObject
public class Divisor implements Serializable {

	public static final int DEFAULT_SCALE = 2;

	public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

	private BigDecimal divisor;

	protected Divisor() {
	}

	public Divisor(int divisor) {
		this.divisor = new BigDecimal(divisor);
	}

	public Amount getQuotient(Amount dividend) {
		return new Amount(
				divisor.divide(dividend.getBigDecimal(), DEFAULT_SCALE, DEFAULT_ROUNDING)
		);
	}

}