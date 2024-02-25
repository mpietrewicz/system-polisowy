package pl.mpietrewicz.sp.modules.balance.domain.balance.state;

import pl.mpietrewicz.sp.ddd.annotations.domain.DomainEntity;
import pl.mpietrewicz.sp.ddd.support.infrastructure.repo.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@DomainEntity
@Entity
public class NewMonth extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "new_month_state_id")
    private NewMonthState newMonthState;

    private LocalDate date;

    public NewMonth() {
        this.newMonthState = new Open(this);
    }

    protected void changeState(NewMonthState newMonthState) {
        this.newMonthState = newMonthState;
    }

    public void pay() {
        newMonthState.pay();
    }

}