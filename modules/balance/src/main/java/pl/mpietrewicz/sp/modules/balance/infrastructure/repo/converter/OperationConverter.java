package pl.mpietrewicz.sp.modules.balance.infrastructure.repo.converter;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
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
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.AddPaymentEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.AddRefundEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.CancelStopCalculatingEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.ChangePremiumEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.OperationEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.PeriodEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.StartCalculatingEntity;
import pl.mpietrewicz.sp.modules.balance.infrastructure.repo.entity.StopCalculatingEntity;

import javax.inject.Inject;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OperationConverter {

    @Inject
    private PeriodConverter periodConverter;

    @Inject
    private AutowireCapableBeanFactory spring;

    public Operation convert(OperationEntity entity) {
        List<Period> periods = entity.getPeriods().stream()
                .map(e -> periodConverter.convert(e))
                .collect(Collectors.toList());

        switch (entity.getType()) {
            case START_CALCULATING:
                return convert((StartCalculatingEntity) entity, periods);
            case ADD_PAYMENT:
                return convert((AddPaymentEntity) entity, periods);
            case ADD_REFUND:
                return convert((AddRefundEntity) entity, periods);
            case CHANGE_PREMIUM:
                return convert((ChangePremiumEntity) entity, periods);
            case STOP_CALCULATING:
                return convert((StopCalculatingEntity) entity, periods);
            case CANCEL_STOP_CALCULATING:
                return convert((CancelStopCalculatingEntity) entity, periods);
            default:
                throw new IllegalStateException();
        }
    }

    private AddPayment convert(AddPaymentEntity entity, List<Period> periods) {
        AddPayment addPayment = new AddPayment(entity.getEntityId(), entity.getDate(), entity.getRegistration(),
                new Amount(entity.getAmount()), entity.getPaymentPolicyEnum(), periods);
        spring.autowireBean(addPayment);
        return addPayment;
    }

    private AddRefund convert(AddRefundEntity entity, List<Period> periods) {
        AddRefund addRefund = new AddRefund(entity.getEntityId(), entity.getDate(), entity.getRegistration(),
                new Amount(entity.getAmount()), periods);
        spring.autowireBean(addRefund);
        return addRefund;
    }

    private StartCalculating convert(StartCalculatingEntity entity, List<Period> periods) {
        StartCalculating startCalculating = new StartCalculating(entity.getEntityId(), YearMonth.from(entity.getDate()),
                entity.getRegistration(), new Amount(entity.getPremium()), periods);
        spring.autowireBean(startCalculating);
        return startCalculating;
    }

    private ChangePremium convert(ChangePremiumEntity entity, List<Period> periods) {
        ChangePremium changePremium = new ChangePremium(entity.getEntityId(), entity.getDate(),
                entity.getRegistration(), periods);
        spring.autowireBean(changePremium);
        return changePremium;
    }

    private StopCalculating convert(StopCalculatingEntity entity, List<Period> periods) {
        StopCalculating stopCalculating = new StopCalculating(entity.getEntityId(), entity.getEnd(),
                entity.getRegistration(), new Amount(entity.getExcess()), entity.isValid(), periods);
        spring.autowireBean(stopCalculating);
        return stopCalculating;
    }

    private CancelStopCalculating convert(CancelStopCalculatingEntity entity, List<Period> periods) {
        CancelStopCalculating cancelStopCalculating = new CancelStopCalculating(entity.getEntityId(),
                entity.getCanceledEnd(), entity.getRegistration(), entity.isValid(), periods);
        spring.autowireBean(cancelStopCalculating);
        return cancelStopCalculating;
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

    private OperationEntity convert(StartCalculating model, List<PeriodEntity> periods) {
        return new StartCalculatingEntity(model.getId(), model.getRegistration(), model.getOperationType(), model.getDate(), periods,
                model.getPremium().getBigDecimal());
    }

    private OperationEntity convert(AddPayment model, List<PeriodEntity> periods) {
        return new AddPaymentEntity(model.getId(), model.getRegistration(), model.getOperationType(), model.getDate(), periods,
                model.getAmount().getBigDecimal(), model.getPaymentPolicyEnum());
    }

    private OperationEntity convert(AddRefund model, List<PeriodEntity> periods) {
        return new AddRefundEntity(model.getId(), model.getRegistration(), model.getOperationType(), model.getDate(), periods,
                model.getRefund().getBigDecimal());
    }

    private OperationEntity convert(ChangePremium model, List<PeriodEntity> periods) {
        return new ChangePremiumEntity(model.getId(), model.getRegistration(), model.getOperationType(), model.getDate(), periods);
    }

    private OperationEntity convert(StopCalculating model, List<PeriodEntity> periods) {
        return new StopCalculatingEntity(model.getId(), model.getRegistration(), model.getOperationType(), model.getDate(), periods,
                model.getExcess().getBigDecimal(), model.getEnd(), model.isValid());
    }

    private OperationEntity convert(CancelStopCalculating model, List<PeriodEntity> periods) {
        return new CancelStopCalculatingEntity(model.getId(), model.getRegistration(), model.getOperationType(), model.getDate(), periods,
                model.getCanceledEnd(), model.isValid());
    }

}