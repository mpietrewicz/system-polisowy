package pl.mpietrewicz.sp.modules.balance.domain.balance.state;

import lombok.NoArgsConstructor;
import pl.mpietrewicz.sp.modules.balance.domain.balance.month.PaidStatus;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public abstract class NewMonthState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaidStatus status;

    @OneToOne(mappedBy = "newMonthState", cascade = CascadeType.ALL, orphanRemoval = true)
    protected NewMonth newMonth;

    public NewMonthState(NewMonth newMonth, PaidStatus status) {
        this.newMonth = newMonth;
        this.status = status;
    }

    public abstract void pay();

}