package pl.mpietrewicz.sp.modules.balance.domain.balance.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.ddd.annotations.domain.ValueObject;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus.PAID;

@ValueObject
@Entity
@DiscriminatorValue("OPENED_PLAYER")
@NoArgsConstructor
public class Open extends NewMonthState {

    private final static PaidStatus status = PAID;

    public Open(NewMonth newMonth) {
        super(newMonth, status);
    }

    @Override
    public void pay() {
        newMonth.changeState(new Close(newMonth));
    }

}