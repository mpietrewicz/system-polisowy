package pl.mpietrewicz.sp.modules.finance.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.mpietrewicz.sp.ddd.annotations.application.Finder;
import pl.mpietrewicz.sp.modules.finance.ddd.support.domain.BaseAggregateRoot;
import pl.mpietrewicz.sp.modules.finance.domain.payment.RegisterPayment;
import pl.mpietrewicz.sp.modules.finance.readmodel.dto.WplataDto;

import java.util.List;

@Finder
public interface FinanceFinder extends JpaRepository<RegisterPayment, BaseAggregateRoot> {

    @Query("SELECT new pl.mpietrewicz.sp.modules.finance.readmodel.dto.WplataDto(p.aggregateId.aggregateId, p.date, p.register, " +
            "p.amount.value, p.contractData.aggregateId.aggregateId) " +
            "FROM RegisterPayment p " +
            "WHERE p.contractData.aggregateId.aggregateId = :contractId")
    List<WplataDto> findPayments(@Param("contractId") String contractId);

//    @Query("SELECT new pl.mpietrewicz.sp.finance.readmodel.dto.PrzypisDto(pd.entityId, " +
//            "pd.period.periodFrom, pd.period.periodTo, pd.status, pd.premium) " +
//            "FROM Due d " +
//            "JOIN d.componentDues cd " +
//            "JOIN cd.premiumDues pd " +
//            "WHERE cd.componentData.aggregateId.aggregateId = :componentId")
//    List<PrzypisDto> findPremiumDue(@Param("componentId") String componentId);

//    @Query("SELECT new pl.mpietrewicz.sp.finance.readmodel.dto.SaldoDto(b.duesSum, b.paymentsSum, b.balance, " +
//            "b.periodPaid.paidTo, b.periodPaid.excess) " +
//            "FROM BalanceOld b " +
//            "WHERE b.contractData.aggregateId.aggregateId = :contractId")
//    SaldoDto findBalance(@Param("contractId") String contractId);

}