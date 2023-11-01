package pl.mpietrewicz.sp.modules.contract.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.mpietrewicz.sp.ddd.annotations.application.Finder;
import pl.mpietrewicz.sp.ddd.support.domain.BaseAggregateRoot;
import pl.mpietrewicz.sp.modules.contract.domain.contract.Contract;
import pl.mpietrewicz.sp.modules.contract.readmodel.dto.PolisaDto;
import pl.mpietrewicz.sp.modules.contract.readmodel.dto.SkladnikDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Finder
public interface ContractFinder extends JpaRepository<Contract, BaseAggregateRoot> {

    @Query("SELECT new pl.mpietrewicz.sp.modules.contract.readmodel.dto.PolisaDto(c.aggregateId.aggregateId, c.startDate, c.contractStatus) " +
            "FROM Contract c")
    List<PolisaDto> find();

    @Query("SELECT new pl.mpietrewicz.sp.modules.contract.readmodel.dto.PolisaDto(c.aggregateId.aggregateId, c.startDate, c.contractStatus) " +
            "FROM Contract c " +
            "WHERE c.aggregateId.aggregateId = :contractId")
    PolisaDto find(@Param("contractId") String contractId);

    @Query(value = "SELECT SUM(ph.monthly_amount) " +
            "FROM premium_history ph, premium p, component cp, contract c " +
            "WHERE ph.premium_id = p.aggregate_id " +
            "AND p.component_id = cp.aggregate_id " +
            "AND cp.contract_id = c.aggregate_id " +
            "AND cp.component_status = 'ACTIVE' " +
            "AND c.aggregate_id = :contractId " +
            "AND ph.entity_id = ( " +
            "   SELECT ph1.entity_id " +
            "   FROM premium_history ph1, premium p1" +
            "   WHERE ph1.premium_id = p1.aggregate_id" +
            "   AND p1.component_id = cp.aggregate_id " +
            "   AND ph1.since <= :atDate" +
            "   ORDER BY ph1.since DESC " +
            "   LIMIT 1 " +
            ")", nativeQuery = true)
    BigDecimal findContractPremium(@Param("contractId") String contractId, @Param("atDate") LocalDate atDate);

    @Query("SELECT new pl.mpietrewicz.sp.modules.contract.readmodel.dto.SkladnikDto(cp.aggregateId.aggregateId, " +
            "cp.contractData.aggregateId.aggregateId, cp.startDate, cp.componentStatus, cp.componentType) " +
            "FROM Component cp " +
            "WHERE cp.contractData.aggregateId.aggregateId = :contractId")
    List<SkladnikDto> findComponents(@Param("contractId") String contractId);

    @Query("SELECT new pl.mpietrewicz.sp.modules.contract.readmodel.dto.SkladnikDto(cp.aggregateId.aggregateId, " +
            "cp.contractData.aggregateId.aggregateId, cp.startDate, cp.componentStatus, cp.componentType) " +
            "FROM Component cp " +
            "WHERE cp.aggregateId.aggregateId = :componentId")
    SkladnikDto findComponent(@Param("componentId") String componentId); // todo: wydzielić do oddzielnego findera

    @Query(value = "SELECT ph.monthly_amount " +
            "FROM premium_history ph, premium p " +
            "WHERE ph.premium_id = p.aggregate_id " +
            "AND p.component_id = :componentId " +
            "AND ph.since <= :atDate " +
            "ORDER BY ph.since DESC " +
            "LIMIT 1", nativeQuery = true)
    BigDecimal findComponentPremium(@Param("componentId") String componentId, @Param("atDate") LocalDate atDate);

}