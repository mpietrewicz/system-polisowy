package pl.mpietrewicz.sp.ddd.sharedkernel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotPositiveAmountException extends Exception {

    public NotPositiveAmountException(BigDecimal value) {
        super(value + " is not positive amount!");
    }

}