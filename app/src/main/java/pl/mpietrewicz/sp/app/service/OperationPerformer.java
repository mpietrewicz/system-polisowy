package pl.mpietrewicz.sp.app.service;

import pl.mpietrewicz.sp.app.readmodel.model.Operation;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.ddd.sharedkernel.exception.NotPositiveAmountException;

public interface OperationPerformer {

    void perform(Operation operation, AggregateId contractId) throws NotPositiveAmountException;

}