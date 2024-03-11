package pl.mpietrewicz.sp.modules.balance.domain.balance.operation.type;

import pl.mpietrewicz.sp.modules.balance.domain.balance.operation.Operation;

import java.time.LocalDate;

interface StopCalculatingService {

    void invalidate();

    int orderComparator(Operation operation);

    Integer getPriority();

    LocalDate getEnd();

}