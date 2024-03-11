package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.converter;

import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.ddd.sharedkernel.Amount;
import pl.mpietrewicz.sp.modules.balance.domain.balance.Period;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddPayment;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.AddRefund;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.CancelStopCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.ChangePremium;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StartCalculating;
import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type.StopCalculating;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.OperationEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.PeriodEntity;

import javax.inject.Inject;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_PAYMENT;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.ADD_REFUND;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CANCEL_STOP_CALCULATING;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.CHANGE_PREMIUM;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.START_CALCULATING;
import static pl.mpietrewicz.sp.modules.balance.domain.balance.operation.OperationType.STOP_CALCULATING;

@Component
public class OperationConverter {

    @Inject
    private PeriodConverter periodConverter;

    public Operation convert(OperationEntity entity) {
        List<Period> periods = entity.getPeriods().stream()
                .map(e -> periodConverter.convert(e))
                .collect(Collectors.toList());
        return createOperation(entity, periods);
    }

    private Operation createOperation(OperationEntity entity, List<Period> periods) {
        switch (entity.getType()) {
            case START_CALCULATING:
                return new StartCalculating(entity.getEntityId(), YearMonth.from(entity.getDate()), entity.getRegistration(), new Amount(entity.getAmount()), periods);
            case ADD_PAYMENT:
                return new AddPayment(entity.getEntityId(), entity.getDate(), entity.getRegistration(), new Amount(entity.getAmount()), entity.getPaymentPolicyEnum(), periods);
            case ADD_REFUND:
                return new AddRefund(entity.getEntityId(), entity.getDate(), entity.getRegistration(), new Amount(entity.getAmount()), periods);
            case CHANGE_PREMIUM:
                return new ChangePremium(entity.getEntityId(), entity.getDate(), entity.getRegistration(), new Amount(entity.getAmount()), entity.getPremiumId(), entity.getTimestamp(), periods);
            case STOP_CALCULATING:
                return new StopCalculating(entity.getEntityId(), entity.getEnd(), entity.isValid(), entity.getRegistration(), new Amount(entity.getAmount()), periods);
            case CANCEL_STOP_CALCULATING:
                return new CancelStopCalculating(entity.getEntityId(), entity.getCanceledEnd(), entity.isValid(), entity.getRegistration(), periods);
            default:
                throw new IllegalStateException();
        }
    }

    public OperationEntity convert(Operation model) {
        List<PeriodEntity> periods = model.getPeriods().stream()
                .map(m -> periodConverter.convert(m))
                .collect(Collectors.toList());

        if (model instanceof StartCalculating) {
            return convert((StartCalculating) model, periods);
        } else if (model instanceof AddPayment) {
            return convert((AddPayment) model, periods);
        } else if (model instanceof AddRefund) {
            return convert((AddRefund) model, periods);
        } else if (model instanceof ChangePremium) {
            return convert((ChangePremium) model, periods);
        } else if (model instanceof StopCalculating) {
            return convert((StopCalculating) model, periods);
        } else if (model instanceof CancelStopCalculating) {
            return convert((CancelStopCalculating) model, periods);
        } else {
            throw new IllegalStateException();
        }
    }

    public OperationEntity convert(StartCalculating model, List<PeriodEntity> periods) {
        return new OperationEntity(model.getId(), model.getDate(), model.getRegistration(), periods, START_CALCULATING,
                model.getPremium().getBigDecimal(), null, null, null, null, null, true);
    }

    public OperationEntity convert(AddPayment model, List<PeriodEntity> periods) {
        return new OperationEntity(model.getId(), model.getDate(), model.getRegistration(), periods, ADD_PAYMENT,
                model.getAmount().getBigDecimal(), model.getPaymentPolicyEnum(), null, null, null, null, true);
    }

    public OperationEntity convert(AddRefund model, List<PeriodEntity> periods) {
        return new OperationEntity(model.getId(), model.getDate(), model.getRegistration(), periods, ADD_REFUND,
                model.getRefund().getBigDecimal(), null, null, null, null, null, true);
    }

    public OperationEntity convert(ChangePremium model, List<PeriodEntity> periods) {
        return new OperationEntity(model.getId(), model.getDate(), model.getRegistration(), periods, CHANGE_PREMIUM,
                model.getPremium().getBigDecimal(), null, model.getPremiumId(), model.getTimestamp(), null, null, true);
    }

    public OperationEntity convert(StopCalculating model, List<PeriodEntity> periods) {
        return new OperationEntity(model.getId(), model.getDate(), model.getRegistration(), periods, STOP_CALCULATING,
                model.getExcess().getBigDecimal(), null, null, null, model.getEnd(), null, model.isValid());
    }

    public OperationEntity convert(CancelStopCalculating model, List<PeriodEntity> periods) {
        return new OperationEntity(model.getId(), model.getDate(), model.getRegistration(), periods, CANCEL_STOP_CALCULATING,
                null, null, null, null, null, model.getCanceledEnd(), model.isValid());
    }

}