package pl.mpietrewicz.sp.modules.balance.domain.balance.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus.UNPAID;

@ValueObject
@Entity
@DiscriminatorValue("CLOSED_PLAYER")
@NoArgsConstructor
public class Close extends NewMonthState {

    private final static PaidStatus status = UNPAID;

    public Close(NewMonth newMonth) {
        super(newMonth, status);
    }

    public void pay() {
        newMonth.changeState(new Open(newMonth));
    }

}