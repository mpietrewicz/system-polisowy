package pl.mpietrewicz.sp.modules.oldbalance.domain.balance;

public interface DueChangeStrategy {

    void execute(PremiumDue premiumDue);

}